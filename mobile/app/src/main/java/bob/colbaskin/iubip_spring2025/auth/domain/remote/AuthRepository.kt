package bob.colbaskin.iubip_spring2025.auth.domain.remote

import bob.colbaskin.iubip_spring2025.auth.data.models.CodeToTokenDTO

interface AuthRepository {

    suspend fun codeToToken(request: CodeToTokenDTO): String
}
