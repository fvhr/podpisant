package bob.colbaskin.iubip_spring2025.profile.domain.remote

import bob.colbaskin.iubip_spring2025.profile.domain.models.Profile

interface ProfileRepository {

    suspend fun getCurrentUser(): Profile
}