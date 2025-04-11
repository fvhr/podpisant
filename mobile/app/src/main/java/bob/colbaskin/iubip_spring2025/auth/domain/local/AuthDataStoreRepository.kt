package bob.colbaskin.iubip_spring2025.auth.domain.local

import kotlinx.coroutines.flow.Flow

interface AuthDataStoreRepository {

    fun getToken(): Flow<String?>
    suspend fun saveToken(token: String)

    fun getRefreshToken(): Flow<String?>
    suspend fun saveRefreshToken(refreshToken: String)

    fun getCodeVerifier(): Flow<String?>
    suspend fun saveCodeVerifier(codeVerifier: String)

    suspend fun clearAll()
}