package bob.colbaskin.iubip_spring2025.organizations.domain.remote

import bob.colbaskin.iubip_spring2025.organizations.data.models.OrganizationResponse
import bob.colbaskin.iubip_spring2025.organizations.domain.models.Organization

interface OrganizationsRepository {

    suspend fun getAllOrganizationsForCurrentUser(): List<Organization>

    suspend fun createOrganization(
        name: String,
        description: String
    ): Int

    suspend fun getOrganizationById(id: Int): Organization
}