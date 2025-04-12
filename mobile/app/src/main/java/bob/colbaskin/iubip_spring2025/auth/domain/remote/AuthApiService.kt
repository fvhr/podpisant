package bob.colbaskin.iubip_spring2025.auth.domain.remote

import bob.colbaskin.iubip_spring2025.auth.data.models.CodeValidationResponseDTO
import bob.colbaskin.iubip_spring2025.auth.data.models.EmailDTO
import bob.colbaskin.iubip_spring2025.auth.data.models.LoginResponseDTO
import bob.colbaskin.iubip_spring2025.auth.data.models.LoginWithCodeDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("login")
    suspend fun loginByEmail(@Body email: EmailDTO): LoginResponseDTO

    @POST("login-with-code")
    suspend fun loginWithCode(@Body request: LoginWithCodeDTO): CodeValidationResponseDTO
}