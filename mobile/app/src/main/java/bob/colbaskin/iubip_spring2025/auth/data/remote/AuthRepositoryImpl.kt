package bob.colbaskin.iubip_spring2025.auth.data.remote

import android.util.Log
import bob.colbaskin.iubip_spring2025.auth.data.models.EmailDTO
import bob.colbaskin.iubip_spring2025.auth.data.models.LoginResponseDTO
import bob.colbaskin.iubip_spring2025.auth.data.models.LoginWithCodeDTO
import bob.colbaskin.iubip_spring2025.auth.data.models.mappers.toDomain
import bob.colbaskin.iubip_spring2025.auth.domain.models.LoginResponse
import bob.colbaskin.iubip_spring2025.auth.domain.remote.AuthApiService
import bob.colbaskin.iubip_spring2025.auth.domain.remote.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApiService
): AuthRepository {

    override suspend fun loginByEmail(email: String): LoginResponse {
        val response = authApi.loginByEmail(EmailDTO(email))
        Log.d("Logging", "Response from loginByEmail in AuthRepositoryImpl: $response")
        return response.toDomain()
    }

    override suspend fun loginWithCode(code: String, deviceId: String): Boolean {
        val response = authApi.loginWithCode(LoginWithCodeDTO(code, deviceId))
        Log.d("Logging", "Response from loginWithCode in AuthRepositoryImpl: ${response.status == "success"}")
        Log.d("Logging", "DeviceId from loginWithCode in AuthRepositoryImpl: $deviceId")
        return response.status == "success"
    }
}