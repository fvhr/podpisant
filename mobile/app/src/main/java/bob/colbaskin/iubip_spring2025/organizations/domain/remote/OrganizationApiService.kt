package bob.colbaskin.iubip_spring2025.organizations.domain.remote

import bob.colbaskin.iubip_spring2025.organizations.data.models.CreateOrgBody
import bob.colbaskin.iubip_spring2025.organizations.data.models.OrganizationResponse
import bob.colbaskin.iubip_spring2025.organizations.data.models.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OrganizationApiService {

    @GET("me")
    suspend fun getAllOrganizationsForCurrentUser(): UserResponse

    @POST("organizations")
    suspend fun createOrganization(@Body body: CreateOrgBody): OrganizationResponse
}