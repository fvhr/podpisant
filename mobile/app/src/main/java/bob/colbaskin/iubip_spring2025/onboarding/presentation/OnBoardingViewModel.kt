package bob.colbaskin.iubip_spring2025.onboarding.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.iubip_spring2025.common.models.OnBoardingConfig
import bob.colbaskin.iubip_spring2025.onboarding.domain.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val preferenceRepository: UserPreferencesRepository
): ViewModel() {

    fun action(action: OnBoardingAction) {
        when (action) {
            OnBoardingAction.OnboardingInProgress -> {
                viewModelScope.launch {
                    Log.d("LOG", "OnBoardingInProgress")
                    preferenceRepository.saveOnBoardingStatus(OnBoardingConfig.IN_PROGRESS)
                }
            }
            OnBoardingAction.OnboardingComplete -> {
                viewModelScope.launch {
                    Log.d("LOG", "OnBoardingCompleted")
                    preferenceRepository.saveOnBoardingStatus(OnBoardingConfig.COMPLETED)
                }
            }
        }
    }
}