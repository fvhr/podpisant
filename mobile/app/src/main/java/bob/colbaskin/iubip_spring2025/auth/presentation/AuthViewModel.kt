package bob.colbaskin.iubip_spring2025.auth.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.iubip_spring2025.auth.presentation.otp.AuthAction
import bob.colbaskin.iubip_spring2025.auth.presentation.otp.OtpAction
import bob.colbaskin.iubip_spring2025.auth.presentation.otp.OtpState
import bob.colbaskin.iubip_spring2025.common.models.AuthConfig
import bob.colbaskin.iubip_spring2025.onboarding.domain.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.regex.Pattern

private const val VALID_OTP_CODE = "1414"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
): ViewModel() {
    var email by mutableStateOf("")
        private set
    var isValid by mutableStateOf(false)
        private set

    private val _state = MutableStateFlow(OtpState())
    val state = _state.asStateFlow()

    private val EMAIL_REGEX = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

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
            isValid = if(newCode.none { it == null }) {
                newCode.joinToString("") == VALID_OTP_CODE
            } else null
        ) }
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