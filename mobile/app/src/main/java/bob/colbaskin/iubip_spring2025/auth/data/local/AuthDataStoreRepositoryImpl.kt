package bob.colbaskin.iubip_spring2025.auth.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import bob.colbaskin.iubip_spring2025.auth.domain.local.AuthDataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "token_data_store")

class AuthDataStoreRepositoryImpl @Inject constructor(
    private val context: Context
): AuthDataStoreRepository {

    private object PreferencesKey {
        val token = stringPreferencesKey(name = "token")
        val refreshToken = stringPreferencesKey(name = "refreshToken")
        val codeVerifier = stringPreferencesKey(name = "codeVerifier")
        val deviceId = stringPreferencesKey(name = "device_id")
    }

    override suspend fun saveDeviceId(deviceId: String) {
        context.tokenDataStore.edit { preferences ->
            preferences[PreferencesKey.deviceId] = deviceId
            Log.d("Logging", "Device ID saved: $deviceId")
        }
    }

    override fun getDeviceId(): Flow<String?> {
        return context.tokenDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesKey.deviceId]
            }
    }

    override suspend fun saveToken(token: String) {
         context.tokenDataStore.edit { preferences ->
            preferences[PreferencesKey.token] = token
            Log.d("Logging", "token saved in datastore: $token")
        }
    }

    override fun getToken(): Flow<String?> {
        return context.tokenDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val token = preferences[PreferencesKey.token]
                Log.d("Logging", "Get token from datastore: $token")
                token
            }
    }

    override suspend fun saveRefreshToken(refreshToken: String) {
        context.tokenDataStore.edit { preferences ->
            preferences[PreferencesKey.refreshToken] = refreshToken
            Log.d("Logging", "refresh token saved: $refreshToken")
        }
    }

    override fun getRefreshToken(): Flow<String?> {
        return context.tokenDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val refreshToken = preferences[PreferencesKey.refreshToken]
                Log.d("Logging", "Get refresh token from datastore: $refreshToken")
                refreshToken
            }
    }

    override suspend fun saveCodeVerifier(codeVerifier: String) {
        context.tokenDataStore.edit { preferences ->
            preferences[PreferencesKey.codeVerifier] = codeVerifier
            Log.d("Logging", "Saved code verifier: $codeVerifier")
        }
    }

    override fun getCodeVerifier(): Flow<String?> {
        return context.tokenDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesKey.codeVerifier]
            }
    }


    override suspend fun clearAll() {
        context.tokenDataStore.edit { preferences ->
            preferences[PreferencesKey.token] = ""
            Log.d("Logging", "Cleared token")
            preferences[PreferencesKey.refreshToken] = ""
            Log.d("Logging", "Cleared refresh token")
            preferences[PreferencesKey.codeVerifier] = ""
            Log.d("Logging", "Cleared code verifier")
            preferences[PreferencesKey.deviceId] = ""
            Log.d("Logging", "Cleared device id")
        }
    }
}