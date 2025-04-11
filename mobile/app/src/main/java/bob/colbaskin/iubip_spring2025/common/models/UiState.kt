package bob.colbaskin.iubip_spring2025.common.models

sealed interface UiState {
    data object Loading: UiState
    data class Success(val data: UserPreferences): UiState
    data class Error(val error: String): UiState
}