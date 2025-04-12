package bob.colbaskin.iubip_spring2025.documents.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.iubip_spring2025.documents.domain.models.Document
import bob.colbaskin.iubip_spring2025.documents.domain.models.DocumentStatus
import bob.colbaskin.iubip_spring2025.documents.domain.remote.DocumentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DocumentDetailsState(
    val document: Document? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DocumentDetailsViewModel @Inject constructor(
    private val repository: DocumentsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DocumentDetailsState())
    val state: StateFlow<DocumentDetailsState> = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DocumentDetailsState(isLoading = true)
        )

    fun loadDocument(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val document = repository.getDocumentById(id)
                _state.update { it.copy(document = document, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun signDocument() {
        viewModelScope.launch {
            _state.value.document?.id?.let { id ->
                repository.updateDocumentStatus(id, DocumentStatus.SIGNED)
                loadDocument(id)
            }
        }
    }

    fun rejectDocument() {
        viewModelScope.launch {
            _state.value.document?.id?.let { id ->
                repository.updateDocumentStatus(id, DocumentStatus.UNSIGNED)
                loadDocument(id)
            }
        }
    }
}