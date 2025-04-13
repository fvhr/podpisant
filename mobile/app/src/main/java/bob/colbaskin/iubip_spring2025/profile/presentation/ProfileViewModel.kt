package bob.colbaskin.iubip_spring2025.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.iubip_spring2025.auth.domain.remote.AuthRepository
import bob.colbaskin.iubip_spring2025.profile.domain.models.Profile
import bob.colbaskin.iubip_spring2025.profile.domain.remote.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class ProfileState(
    val profile: Profile? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val profile = withTimeout(5_000) {
                    repository.getCurrentUser()
                }
                _state.update { it.copy(profile = profile, isLoading = false) }
            } catch (e: TimeoutCancellationException) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Превышено время получения данных"
                )}
            } catch (e: IOException) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Ошибка подключения к интернету"
                )}
            } catch (e: HttpException) {
                val errorMsg = when (e.code()) {
                    401 -> {
                        authRepository.refreshToken()
                        "Ошибка авторизации. Попробуйте снова!"
                    }
                    403 -> "Доступ запрещен"
                    else -> "Ошибка сервера: ${e.code()}"
                }
                _state.update { it.copy(isLoading = false, error = errorMsg) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Неизвестная ошибка"
                )}
            }
        }
    }
}