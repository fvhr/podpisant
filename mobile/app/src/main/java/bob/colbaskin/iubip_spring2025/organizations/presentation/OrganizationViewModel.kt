package bob.colbaskin.iubip_spring2025.organizations.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.iubip_spring2025.organizations.domain.models.Organization
import bob.colbaskin.iubip_spring2025.organizations.domain.remote.OrganizationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.io.IOException
import retrofit2.HttpException
import javax.inject.Inject

data class OrganizationsState(
    val organizations: List<Organization> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAdmin: Boolean = true
)

@HiltViewModel
class OrganizationsViewModel @Inject constructor(
    private val organizationsRepository: OrganizationsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(OrganizationsState())
    val state: StateFlow<OrganizationsState> = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = OrganizationsState(isLoading = true)
        )

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog

    init {
        loadOrganizations()
    }

    fun loadOrganizations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val organizations = withTimeout(5_000) {
                    organizationsRepository.getAllOrganizations()
                }
                _state.update {
                    it.copy(
                        organizations = organizations,
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

    fun createOrganization(name: String, description: String) {
        viewModelScope.launch {
            _showCreateDialog.update { false }
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val newOrganization = withTimeout(5_000) {
                    organizationsRepository.createOrganization(name, description)
                }
                _state.update { state ->
                    state.copy(
                        organizations = state.organizations + newOrganization,
                        isLoading = false
                    )
                }
            } catch (e: TimeoutCancellationException) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Превышено время создания организации!"
                    )
                }
            } catch (e: IOException) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка сети при создании организации"
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
                        error = e.message ?: "Неизвестная ошибка создания"
                    )
                }
            }
        }
    }

    fun toggleCreateDialog(show: Boolean) {
        _showCreateDialog.update { show }
    }
}