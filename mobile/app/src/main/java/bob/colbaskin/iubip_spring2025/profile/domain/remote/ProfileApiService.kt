package bob.colbaskin.iubip_spring2025.profile.domain.remote

import bob.colbaskin.iubip_spring2025.profile.data.models.ProfileDTO
import retrofit2.http.GET

interface ProfileApiService {

    @GET("me")
    suspend fun getMe(): ProfileDTO
}