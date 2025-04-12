package bob.colbaskin.iubip_spring2025.documents.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.iubip_spring2025.documents.domain.models.Document
import bob.colbaskin.iubip_spring2025.documents.domain.remote.DocumentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DocumentsState(
    val documents: List<Document> = emptyList(),
    val showCreateDialog: Boolean = false,
    val isAdmin: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val repository: DocumentsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DocumentsState())
    val state: StateFlow<DocumentsState> = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DocumentsState(isLoading = true)
        )

    init {
        loadDocuments()
    }

    fun loadDocuments() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val docs = repository.getAllDocuments()
                _state.update { it.copy(documents = docs, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun toggleCreateDialog(show: Boolean) {
        _state.update { it.copy(showCreateDialog = show) }
    }

    fun createDocument(title: String, author: String) {
        viewModelScope.launch {
            try {
                val newDoc = repository.createDocument(title, author)
                _state.update { it.copy(documents = it.documents + newDoc) }
                toggleCreateDialog(false)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}
