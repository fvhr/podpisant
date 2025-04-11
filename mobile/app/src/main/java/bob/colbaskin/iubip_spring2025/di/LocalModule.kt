package bob.colbaskin.iubip_spring2025.di


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import bob.colbaskin.datastore.UserPreferences
import bob.colbaskin.iubip_spring2025.utils.user.UserDataStore
import bob.colbaskin.iubip_spring2025.utils.user.UserPreferenceSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideUserPreferenceSerializer(): UserPreferenceSerializer {
        return UserPreferenceSerializer()
    }

    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(
        @ApplicationContext context: Context,
        serializer: UserPreferenceSerializer
    ): DataStore<UserPreferences> {
        return DataStoreFactory.create(
            serializer = serializer
        ) {
            context.dataStoreFile("user_preferences.pb")
        }
    }

    @Provides
    @Singleton
    fun provideUserDataStore(
        dataStore: DataStore<UserPreferences>,
        @ApplicationContext context: Context
    ): UserDataStore {
        return UserDataStore(dataStore, context)
    }
}