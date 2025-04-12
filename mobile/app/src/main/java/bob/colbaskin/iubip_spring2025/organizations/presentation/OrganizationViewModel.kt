package bob.colbaskin.iubip_spring2025.organizations.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.iubip_spring2025.organizations.domain.models.Organization
import bob.colbaskin.iubip_spring2025.organizations.domain.remote.OrganizationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
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

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog

    init {
        loadOrganizations()
    }

    private fun loadOrganizations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val organizations = organizationsRepository.getAllOrganizations()
                _state.update {
                    it.copy(
                        organizations = organizations,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun createOrganization(name: String, description: String) {
        viewModelScope.launch {
            delay(1000)
            val newOrganization = Organization(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description
            )
            _state.update {
                it.copy(organizations = it.organizations + newOrganization)
            }
            _showCreateDialog.update { false }
        }
    }

    fun toggleCreateDialog(show: Boolean) {
        _showCreateDialog.update { show }
    }
}