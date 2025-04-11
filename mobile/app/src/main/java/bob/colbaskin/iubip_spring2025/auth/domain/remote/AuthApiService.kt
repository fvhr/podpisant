package bob.colbaskin.iubip_spring2025.auth.domain.remote

import bob.colbaskin.iubip_spring2025.auth.data.models.CodeToTokenDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("code-to-token")
    suspend fun codeToToken(@Body request: CodeToTokenDTO): String
}