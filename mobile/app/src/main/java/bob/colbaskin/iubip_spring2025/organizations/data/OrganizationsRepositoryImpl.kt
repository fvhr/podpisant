package bob.colbaskin.iubip_spring2025.organizations.data

import bob.colbaskin.iubip_spring2025.organizations.data.models.CreateOrgBody
import bob.colbaskin.iubip_spring2025.organizations.domain.models.Organization
import bob.colbaskin.iubip_spring2025.organizations.domain.remote.OrganizationApiService
import bob.colbaskin.iubip_spring2025.organizations.domain.remote.OrganizationsRepository
import kotlinx.coroutines.delay
import retrofit2.HttpException
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

class OrganizationsRepositoryImpl @Inject constructor(
    private val organizationApiService: OrganizationApiService
): OrganizationsRepository {
    private val mockOrganizations = listOf(
        Organization(
            id = 1,
            name = "ООО Ромашка",
            description = "Цветочный магазин премиум класса"
        ),
        Organization(
            id = 2,
            name = "ИП Иванов",
            description = "Строительные и отделочные работы"
        )
    )

    override suspend fun getAllOrganizationsForCurrentUser(): List<Organization> {
        val response = organizationApiService.getAllOrganizationsForCurrentUser()
        return response.userOrganizations.map { it.toDomain() }
    }

    override suspend fun createOrganization(
        name: String,
        description: String
    ): Int {
        return organizationApiService.createOrganization(
            CreateOrgBody(
                name = name,
                description = description,
                adminId = "1e5a3582-96f7-4271-95e4-4247dd008dbb"
            )
        ).organizationId
    }

    override suspend fun getOrganizationById(id: Int): Organization {
        delay(1500)

        return mockOrganizations.find { it.id == id }
            ?: throw HttpException(Response.error<String>(404, null))
    }
}