package bob.colbaskin.iubip_spring2025.organizations.data

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
            id = "1",
            name = "ООО Ромашка",
            description = "Цветочный магазин премиум класса"
        ),
        Organization(
            id = "2",
            name = "ИП Иванов",
            description = "Строительные и отделочные работы"
        )
    )

    override suspend fun getAllOrganizations(): List<Organization> {
        delay(1500)
        return listOf(
            Organization("1", "ООО Ромашка", "Цветочный магазин"),
            Organization("2", "ИП Иванов", "Строительные услуги")
        )
    }

    override suspend fun createOrganization(name: String, description: String): Organization {
        delay(1000)
        return Organization(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description
        )
    }

    override suspend fun getOrganizationById(id: String): Organization {
        delay(1500)

        return mockOrganizations.find { it.id == id }
            ?: throw HttpException(Response.error<String>(404, null))
    }
}