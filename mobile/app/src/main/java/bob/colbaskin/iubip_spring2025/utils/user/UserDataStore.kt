package bob.colbaskin.iubip_spring2025.utils.user

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import bob.colbaskin.datastore.AuthStatus
import bob.colbaskin.datastore.OnboardingStatus
import bob.colbaskin.datastore.UserPreferences
import bob.colbaskin.datastore.copy
import bob.colbaskin.iubip_spring2025.common.models.AuthConfig
import bob.colbaskin.iubip_spring2025.common.models.OnBoardingConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.userDataStore: DataStore<Preferences> by  preferencesDataStore("user_data_store")

class UserDataStore(
    private val dataStore: DataStore<UserPreferences>,
    private val context: Context
) {
    private object PreferencesKeys {
        val name = stringPreferencesKey(name = "name")
        val avatarUrl = stringPreferencesKey(name = "avatar_url")
    }

    fun getUserName(): Flow<String?> {
        return context.userDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.d("LOG", "Couldn`t get name from datastore")
                    emit(emptyPreferences())
                } else {
                    Log.d("LOG", "Couldn`t get name from datastore")
                    throw exception
                }
            }
            .map { preferences ->
                val name = preferences[PreferencesKeys.name]
                Log.d("LOG", "Get name from datastore: $name")
                name
            }
    }

    suspend fun saveUserName(name: String) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.name] = name
            Log.d("LOG", "Name saved: $name")
        }
    }

    fun getAvatarUrl(): Flow<String?> {
        return context.userDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.d("LOG", "Couldn`t get avatar url from datastore")
                    emit(emptyPreferences())
                } else {
                    Log.d("LOG", "Couldn`t get avatar url from datastore")
                    throw exception
                }
            }
            .map { preferences ->
                val url = preferences[PreferencesKeys.avatarUrl]
                Log.d("LOG", "Get avatar url from datastore: $url")
                url
            }
    }

    suspend fun saveAvatarUrl(url: String) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.avatarUrl] = url
            Log.d("LOG", "Avatar url saved: $url")
        }
    }

    fun getUserData(): Flow<UserPreferences> = dataStore.data

    suspend fun saveUserAuthStatus(status: AuthConfig) {
        dataStore.updateData {
            it.copy {
                authStatus = when (status) {
                    AuthConfig.NOT_AUTHENTICATED -> AuthStatus.NOT_AUTHENTICATED
                    AuthConfig.AUTHENTICATED -> AuthStatus.AUTHENTICATED
                }
            }
        }
    }

    suspend fun saveOnBoardingStatus(status: OnBoardingConfig) {
        dataStore.updateData {
            it.copy {
                onboardingStatus = when (status) {
                    OnBoardingConfig.NOT_STARTED -> OnboardingStatus.NOT_STARTED
                    OnBoardingConfig.IN_PROGRESS -> OnboardingStatus.IN_PROGRESS
                    OnBoardingConfig.COMPLETED -> OnboardingStatus.COMPLETED
                }
            }
        }
    }

}