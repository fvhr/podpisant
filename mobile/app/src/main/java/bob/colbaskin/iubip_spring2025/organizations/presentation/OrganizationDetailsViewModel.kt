package bob.colbaskin.iubip_spring2025.organizations.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.iubip_spring2025.organizations.domain.models.Organization
import bob.colbaskin.iubip_spring2025.organizations.domain.remote.OrganizationsRepository
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

data class OrganizationDetailsState(
    val organization: Organization? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OrganizationDetailsViewModel @Inject constructor(
    private val organizationsRepository: OrganizationsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(OrganizationDetailsState())
    val state: StateFlow<OrganizationDetailsState> = _state

    fun loadOrganization(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val organization = withTimeout(5_000) {
                    organizationsRepository.getOrganizationById(id)
                }
                _state.update {
                    it.copy(
                        organization = organization,
                        isLoading = false
                    )
                }
            } catch (e: TimeoutCancellationException) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Превышено время ожидания!"
                    )
                }
            } catch (e: IOException) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Подключение к интернету было потеряно!"
                    )
                }
            } catch (e: HttpException) {
                val errorMessage = when (e.code()) {
                    401 -> "Не удалось получить данные!"
                    else -> "Ошибка HTTP: ${e.code()}"
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Неизвестная ошибка!"
                    )
                }
            }
        }
    }
}