package bob.colbaskin.iubip_spring2025.documents.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.iubip_spring2025.auth.domain.remote.AuthRepository
import bob.colbaskin.iubip_spring2025.documents.domain.models.Document
import bob.colbaskin.iubip_spring2025.documents.domain.remote.DocumentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException
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
    private val repository: DocumentsRepository,
    private val authRepository: AuthRepository
): ViewModel() {
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
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val docs = withTimeout(5_000) {
                    repository.getAllDocuments()
                }
                Log.d("Logging", "Documents loaded: $docs")
                _state.update { it.copy(documents = docs, isLoading = false) }
            } catch (e: TimeoutCancellationException) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Превышено время получения данных"
                )}
                Log.e("Logging", "${e.message}")
            } catch (e: IOException) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Ошибка подключения к интернету"
                )}
                Log.e("Logging", "${e.message}")
            } catch (e: HttpException) {
                val errorMsg = when (e.code()) {
                    401 -> {
                        authRepository.refreshToken()
                        "Ошибка авторизации. Попробуйте снова!"
                    }
                    403 -> "Доступ запрещен"
                    else -> "Ошибка сервера: ${e.code()}"
                }
                Log.e("Logging", "${e.message}")
                _state.update { it.copy(isLoading = false, error = errorMsg) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Неизвестная ошибка"
                )}
                Log.e("Logging", "${e.message}")
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
