package bob.colbaskin.iubip_spring2025.di

import android.content.Context
import bob.colbaskin.iubip_spring2025.auth.data.local.AuthDataStoreRepositoryImpl
import bob.colbaskin.iubip_spring2025.auth.data.remote.AuthRepositoryImpl
import bob.colbaskin.iubip_spring2025.auth.domain.local.AuthDataStoreRepository
import bob.colbaskin.iubip_spring2025.auth.domain.remote.AuthApiService
import bob.colbaskin.iubip_spring2025.auth.domain.remote.AuthRepository
import bob.colbaskin.iubip_spring2025.documents.data.remote.DocumentsRepositoryImpl
import bob.colbaskin.iubip_spring2025.documents.domain.remote.DocumentApiService
import bob.colbaskin.iubip_spring2025.documents.domain.remote.DocumentsRepository
import bob.colbaskin.iubip_spring2025.onboarding.data.UserPreferencesRepositoryImpl
import bob.colbaskin.iubip_spring2025.onboarding.domain.UserPreferencesRepository
import bob.colbaskin.iubip_spring2025.organizations.data.OrganizationsRepositoryImpl
import bob.colbaskin.iubip_spring2025.organizations.domain.remote.OrganizationApiService
import bob.colbaskin.iubip_spring2025.organizations.domain.remote.OrganizationsRepository
import bob.colbaskin.iubip_spring2025.profile.data.remote.ProfileRepositoryImpl
import bob.colbaskin.iubip_spring2025.profile.domain.remote.ProfileApiService
import bob.colbaskin.iubip_spring2025.profile.domain.remote.ProfileRepository
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

    @Provides
    @Singleton
    fun provideOrganizationApiService(retrofit: Retrofit): OrganizationApiService {
        return retrofit.create(OrganizationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOrganizationsRepository(organizationApiService: OrganizationApiService): OrganizationsRepository {
        return OrganizationsRepositoryImpl(organizationApiService)
    }

    @Provides
    @Singleton
    fun provideDocumentApiService(retrofit: Retrofit): DocumentApiService {
        return retrofit.create(DocumentApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDocumentsRepository(documentApiService: DocumentApiService): DocumentsRepository {
        return DocumentsRepositoryImpl(documentApiService)
    }

    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService {
        return retrofit.create(ProfileApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(profileApiService: ProfileApiService): ProfileRepository {
        return ProfileRepositoryImpl(profileApiService)
    }
}