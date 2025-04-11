package bob.colbaskin.iubip_spring2025.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.iubip_spring2025.common.models.UiState
import bob.colbaskin.iubip_spring2025.onboarding.domain.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    repository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<UiState> = repository.getUserPreference().map {
        UiState.Success(it)
    }.distinctUntilChanged().stateIn(
        scope = viewModelScope,
        initialValue = UiState.Loading,
        started = SharingStarted.Companion.WhileSubscribed(5_000)
    )
}