package bob.colbaskin.iubip_spring2025.auth.domain.remote

import bob.colbaskin.iubip_spring2025.auth.domain.models.LoginResponse

interface AuthRepository {

    suspend fun loginByEmail(email: String): LoginResponse

    suspend fun loginWithCode(code: String, deviceId: String): Boolean

    suspend fun refreshToken(): String
}
