package bob.colbaskin.iubip_spring2025.auth.domain.models

data class LoginResponse(
    val deviceId: String,
    val status: String
)
