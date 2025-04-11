package bob.colbaskin.iubip_spring2025.auth.data.remote

import bob.colbaskin.iubip_spring2025.auth.data.models.CodeToTokenDTO
import bob.colbaskin.iubip_spring2025.auth.domain.remote.AuthApiService
import bob.colbaskin.iubip_spring2025.auth.domain.remote.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApiService
): AuthRepository {

    override suspend fun codeToToken(request: CodeToTokenDTO): String {
        return authApi.codeToToken(request)
    }
}