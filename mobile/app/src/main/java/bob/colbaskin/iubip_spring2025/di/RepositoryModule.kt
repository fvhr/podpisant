package bob.colbaskin.iubip_spring2025.di

import android.content.Context
import bob.colbaskin.iubip_spring2025.auth.data.local.AuthDataStoreRepositoryImpl
import bob.colbaskin.iubip_spring2025.auth.data.remote.AuthRepositoryImpl
import bob.colbaskin.iubip_spring2025.auth.domain.local.AuthDataStoreRepository
import bob.colbaskin.iubip_spring2025.auth.domain.remote.AuthApiService
import bob.colbaskin.iubip_spring2025.auth.domain.remote.AuthRepository
import bob.colbaskin.iubip_spring2025.onboarding.data.UserPreferencesRepositoryImpl
import bob.colbaskin.iubip_spring2025.onboarding.domain.UserPreferencesRepository
import bob.colbaskin.iubip_spring2025.utils.user.UserDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(dataStore: UserDataStore): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideAuthDataStoreRepository(@ApplicationContext context: Context): AuthDataStoreRepository {
        return AuthDataStoreRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authApiService: AuthApiService): AuthRepository {
        return AuthRepositoryImpl(authApiService)
    }
}