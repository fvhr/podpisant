package bob.colbaskin.iubip_spring2025.auth.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.iubip_spring2025.auth.data.remote.AuthRepositoryImpl
import bob.colbaskin.iubip_spring2025.auth.domain.local.AuthDataStoreRepository
import bob.colbaskin.iubip_spring2025.auth.domain.models.LoginResponse
import bob.colbaskin.iubip_spring2025.auth.presentation.otp.AuthAction
import bob.colbaskin.iubip_spring2025.auth.presentation.otp.OtpAction
import bob.colbaskin.iubip_spring2025.auth.presentation.otp.OtpState
import bob.colbaskin.iubip_spring2025.common.models.AuthConfig
import bob.colbaskin.iubip_spring2025.onboarding.domain.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException
import java.util.regex.Pattern

data class AuthState(
    val response: LoginResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAdmin: Boolean = true
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val authRepository: AuthRepositoryImpl,
    private val authDataStoreRepository: AuthDataStoreRepository
): ViewModel() {
    var email by mutableStateOf("")
        private set
    var isValid by mutableStateOf(false)
        private set
    private val _deviceIdLocal = MutableStateFlow<String?>(null)
    val deviceId: StateFlow<String?> = authDataStoreRepository.getDeviceId()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun setDeviceLocalId(deviceId: String) {
        viewModelScope.launch {
            authDataStoreRepository.clearAll()
            authDataStoreRepository.saveDeviceId(deviceId)
            Log.d("Logging", "Device ID saved to DataStore: $deviceId")
        }
    }

    private val _state = MutableStateFlow(OtpState())
    val state = _state.asStateFlow()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthState(isLoading = true)
        )

    private val EMAIL_REGEX = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun loginByEmail(email: String) {
        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, error = null) }
            try {
                val response: LoginResponse = withTimeout(5_000) {
                    authRepository.loginByEmail(email)
                }
                Log.d("Logging", "Response from VM: $response")
                _authState.update {
                    it.copy(
                        response = response,
                        isLoading = false
                    )
                }
            } catch (e: TimeoutCancellationException) {
                _authState.update {
                    it.copy(
                        isLoading = false,
                        error = "Превышено время ожидания!"
                    )
                }
            } catch (e: IOException) {
                _authState.update {
                    it.copy(
                        isLoading = false,
                        error = "Подключение к интернету было потеряно!"
                    )
                }
            } catch (e: HttpException) {
                val errorMessage = when (e.code()) {
                    401 -> "Не удалось получить данные!"
                    404 -> "Неправильный email! Не получится Вас авторизовать!"
                    else -> "Ошибка HTTP ${e.code()}"
                }
                _authState.update {
                    it.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            } catch (e: Exception) {
                _authState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Неизвестная ошибка!"
                    )
                }
            }
        }
    }

    fun loginWithCode(code: String) {
        viewModelScope.launch {
            try {
                val deviceIdd = runBlocking { authDataStoreRepository.getDeviceId().first() }
                val isValid: Boolean = withTimeout(5_000) {
                    authRepository.loginWithCode(code, deviceIdd.toString())
                }
                Log.d("Logging", "Starting loginWithCode: code=$code, deviceId=${deviceIdd}")
                Log.d("Logging", "loginWithCode response isValid: $isValid")
                _authState.update {
                    it.copy(
                        isLoading = false,
                    )
                }
                _state.update { it.copy(isValid = isValid) }
            } catch (e: TimeoutCancellationException) {
                _authState.update {
                    it.copy(
                        isLoading = false,
                        error = "Превышено время ожидания!"
                    )
                }
            } catch (e: IOException) {
                _authState.update {
                    it.copy(
                        isLoading = false,
                        error = "Подключение к интернету было потеряно!"
                    )
                }
            } catch (e: HttpException) {
                val errorMessage = when (e.code()) {
                    401 -> "Не удалось получить данные!"
                    else -> "Ошибка HTTP ${e.code()}"
                }
                _authState.update {
                    it.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            } catch (e: Exception) {
                _authState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Неизвестная ошибка!"
                    )
                }
            }
        }
    }

    fun updateEmail(newEmail: String) {
        email = newEmail
        isValid = validateEmail(newEmail)
    }

    private fun validateEmail(email: String): Boolean {
        return EMAIL_REGEX.matcher(email).matches()
    }

    fun onAction(action: OtpAction) {
        when(action) {
            is OtpAction.OnChangeFieldFocused -> {
                _state.update { it.copy(
                    focusedIndex = action.index
                ) }
            }
            is OtpAction.OnEnterNumber -> {
                enterNumber(action.number, action.index)
            }
            OtpAction.OnKeyboardBack -> {
                val previousIndex = getPreviousFocusedIndex(state.value.focusedIndex)
                _state.update { it.copy(
                    code = it.code.mapIndexed { index, number ->
                        if(index == previousIndex) {
                            null
                        } else {
                            number
                        }
                    },
                    focusedIndex = previousIndex
                ) }
            }
        }
    }

    fun action(action: AuthAction) {
        when(action) {
            AuthAction.Authenticated -> {
                viewModelScope.launch {
                    Log.d("Logging", "Authenticated status from VM")
                    preferencesRepository.saveUserAuthStatus(AuthConfig.AUTHENTICATED)
                }
            }
            AuthAction.NotAuthenticated -> {
                viewModelScope.launch {
                    Log.d("Logging", "NotAuthenticated status from VM")
                    preferencesRepository.saveUserAuthStatus(AuthConfig.NOT_AUTHENTICATED)
                }
            }
        }
    }

    private fun enterNumber(number: Int?, index: Int) {
        Log.d("Logging", "Entering number: $number at index: $index")
        val newCode = state.value.code.mapIndexed { currentIndex, currentNumber ->
            if(currentIndex == index) {
                number
            } else {
                currentNumber
            }
        }
        val wasNumberRemoved = number == null
        _state.update { it.copy(
            code = newCode,
            focusedIndex = if(wasNumberRemoved || it.code.getOrNull(index) != null) {
                it.focusedIndex
            } else {
                getNextFocusedTextFieldIndex(
                    currentCode = it.code,
                    currentFocusedIndex = it.focusedIndex
                )
            },
            isValid = null
        ) }
        if(newCode.none { it == null }) {
            viewModelScope.launch {
                try {
                    val codeStr = newCode.joinToString("")
                    Log.d("Logging", "All code digits entered: $codeStr, verifying...")
                    _authState.update { it.copy(isLoading = true, error = null) }
                    runBlocking {
                        loginWithCode(codeStr)
                    }
                    Log.d("Logging", "Try to redirect to next screen: ${isValid}, verifying...")
                    _state.update { it.copy(isValid = isValid) }
                } catch (e: Exception) {
                    _authState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
            }
        }
    }

    private fun getPreviousFocusedIndex(currentIndex: Int?): Int? {
        return currentIndex?.minus(1)?.coerceAtLeast(0)
    }

    private fun getNextFocusedTextFieldIndex(
        currentCode: List<Int?>,
        currentFocusedIndex: Int?
    ): Int? {
        if(currentFocusedIndex == null) {
            return null
        }

        if(currentFocusedIndex == 3) {
            return currentFocusedIndex
        }

        return getFirstEmptyFieldIndexAfterFocusedIndex(
            code = currentCode,
            currentFocusedIndex = currentFocusedIndex
        )
    }

    private fun getFirstEmptyFieldIndexAfterFocusedIndex(
        code: List<Int?>,
        currentFocusedIndex: Int
    ): Int {
        code.forEachIndexed { index, number ->
            if(index <= currentFocusedIndex) {
                return@forEachIndexed
            }
            if(number == null) {
                return index
            }
        }
        return currentFocusedIndex
    }
}