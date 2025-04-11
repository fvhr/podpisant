package bob.colbaskin.iubip_spring2025.di

import android.util.Log
import bob.colbaskin.iubip_spring2025.BuildConfig
import bob.colbaskin.iubip_spring2025.auth.domain.local.AuthDataStoreRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    @Named("apiUrl")
    fun provideApiUrl(): String {
        return "${BuildConfig.BASE_API_URL}/api/"
    }

    @Provides
    @Singleton
    @Named("accessToken")
    fun provideAccessToken(authDataStoreRepository: AuthDataStoreRepository): String {
        return runBlocking {
            authDataStoreRepository.getToken().first().toString()
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authDataStoreRepository: AuthDataStoreRepository,
        @Named("accessToken") token: String
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val token2 = runBlocking { authDataStoreRepository.getToken().first() } ?: token
                Log.d("AuthViewModel", "token used in provideOkHttpClient: $token and $token2")

                val cookie = Cookie.Builder()
                    .domain(BuildConfig.DOMAIN)
                    .name("access_token")
                    .value(token2)
                    .build()

                val request = chain.request().newBuilder()
                    .build()

                val cookieJar = PersistentCookieJar()
                cookieJar.saveFromResponse(chain.request().url, listOf(cookie))

                chain.proceed(request)
            })
            .cookieJar(PersistentCookieJar())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @Named("apiUrl") apiUrl: String,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(apiUrl)
            .client(okHttpClient)
            .build()
    }
}

class PersistentCookieJar : CookieJar {

    private val cookies = mutableListOf<Cookie>()

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookies.filter { it.matches(url) }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        this.cookies.addAll(cookies)
    }
}