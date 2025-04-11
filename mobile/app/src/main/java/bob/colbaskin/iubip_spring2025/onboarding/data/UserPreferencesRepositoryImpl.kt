package bob.colbaskin.iubip_spring2025.onboarding.data

import bob.colbaskin.iubip_spring2025.common.models.AuthConfig
import bob.colbaskin.iubip_spring2025.common.models.OnBoardingConfig
import bob.colbaskin.iubip_spring2025.common.models.UserPreferences
import bob.colbaskin.iubip_spring2025.onboarding.domain.UserPreferencesRepository
import bob.colbaskin.datastore.AuthStatus
import bob.colbaskin.datastore.OnboardingStatus
import bob.colbaskin.iubip_spring2025.utils.user.UserDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: UserDataStore
): UserPreferencesRepository {
    override fun getUserPreference(): Flow<UserPreferences> {
        return flow {
            emit(
                dataStore.getUserData().first().let {

                    UserPreferences(
                        onBoardingStatus = when (it.onboardingStatus) {
                            OnboardingStatus.NOT_STARTED -> OnBoardingConfig.NOT_STARTED
                            OnboardingStatus.IN_PROGRESS -> OnBoardingConfig.IN_PROGRESS
                            OnboardingStatus.COMPLETED -> OnBoardingConfig.COMPLETED
                            OnboardingStatus.UNRECOGNIZED, null -> OnBoardingConfig.NOT_STARTED
                        },
                        authStatus = when(it.authStatus) {
                            AuthStatus.AUTHENTICATED -> AuthConfig.AUTHENTICATED
                            AuthStatus.NOT_AUTHENTICATED -> AuthConfig.NOT_AUTHENTICATED
                            AuthStatus.UNRECOGNIZED, null -> AuthConfig.NOT_AUTHENTICATED
                        }
                    )
                }
            )
        }
    }

    override suspend fun saveUserAuthStatus(status: AuthConfig) {
        return dataStore.saveUserAuthStatus(status)
    }

    override suspend fun saveOnBoardingStatus(status: OnBoardingConfig) {
        return dataStore.saveOnBoardingStatus(status)
    }
}