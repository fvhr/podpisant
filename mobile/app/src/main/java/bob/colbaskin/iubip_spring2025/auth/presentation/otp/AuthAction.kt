package bob.colbaskin.iubip_spring2025.auth.presentation.otp

interface AuthAction {
    data object NotAuthenticated : AuthAction
    data object Authenticated : AuthAction
}