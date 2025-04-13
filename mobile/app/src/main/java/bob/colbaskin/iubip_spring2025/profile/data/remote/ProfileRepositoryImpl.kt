package bob.colbaskin.iubip_spring2025.profile.data.remote

import android.util.Log
import bob.colbaskin.iubip_spring2025.profile.data.models.toDomain
import bob.colbaskin.iubip_spring2025.profile.domain.remote.ProfileApiService
import bob.colbaskin.iubip_spring2025.profile.domain.remote.ProfileRepository
import bob.colbaskin.iubip_spring2025.profile.domain.models.Profile
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApiService
) : ProfileRepository {
    override suspend fun getCurrentUser(): Profile {
        val dto = profileApi.getMe()
        Log.d("ProfileRepositoryImpl", "getMe: $dto")
        return dto.toDomain()
    }
}
