package bob.colbaskin.iubip_spring2025.organizations.domain.remote

import bob.colbaskin.iubip_spring2025.organizations.domain.models.Organization

interface OrganizationsRepository {

    suspend fun getAllOrganizations(): List<Organization>
}