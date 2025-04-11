package bob.colbaskin.iubip_spring2025.common.models

data class UserPreferences(
    val onBoardingStatus: OnBoardingConfig,
    val authStatus: AuthConfig
)
