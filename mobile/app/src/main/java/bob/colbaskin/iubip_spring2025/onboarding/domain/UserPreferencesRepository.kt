package bob.colbaskin.iubip_spring2025.onboarding.domain

import bob.colbaskin.iubip_spring2025.common.models.AuthConfig
import bob.colbaskin.iubip_spring2025.common.models.OnBoardingConfig
import bob.colbaskin.iubip_spring2025.common.models.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getUserPreference(): Flow<UserPreferences>

    suspend fun saveUserAuthStatus(status: AuthConfig)

    suspend fun saveOnBoardingStatus(status: OnBoardingConfig)
}