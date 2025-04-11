package bob.colbaskin.iubip_spring2025.onboarding.presentation

interface OnBoardingAction {
    data object OnboardingInProgress: OnBoardingAction
    data object OnboardingComplete: OnBoardingAction
}