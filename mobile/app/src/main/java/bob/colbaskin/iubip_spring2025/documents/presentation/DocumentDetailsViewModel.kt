package bob.colbaskin.iubip_spring2025.documents.presentation

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.iubip_spring2025.auth.domain.remote.AuthRepository
import bob.colbaskin.iubip_spring2025.documents.domain.models.Document
import bob.colbaskin.iubip_spring2025.documents.domain.models.Document.DocumentStatus
import bob.colbaskin.iubip_spring2025.documents.domain.remote.DocumentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException
import java.util.jar.Manifest
import javax.inject.Inject

data class DocumentDetailsState(
    val document: Document? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val downloadLink: String? = null,
)

@HiltViewModel
class DocumentDetailsViewModel @Inject constructor(
    private val repository: DocumentsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DocumentDetailsState())
    val state: StateFlow<DocumentDetailsState> = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DocumentDetailsState(isLoading = true)
        )

    fun loadDocument(id: Int) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                val document = withTimeout(5_000) {
                    repository.getDocumentById(id)
                }
                Log.d("Logging", "Document loaded: $document")
                _state.update { it.copy(document = document, isLoading = false) }
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

    fun getDownloadLink() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                val link = withTimeout(5_000) {
                    repository.getDownloadLink(_state.value.document?.id ?: 0)
                }
                Log.d("Logging", "Link loaded: $link")
                _state.update { it.copy(downloadLink = link, isLoading = false) }
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
                repository.updateDocumentStatus(id, DocumentStatus.REJECTED)
                loadDocument(id)
            }
        }
    }
}