package bob.colbaskin.iubip_spring2025.organizations.data

import bob.colbaskin.iubip_spring2025.organizations.domain.models.Organization
import bob.colbaskin.iubip_spring2025.organizations.domain.remote.OrganizationApiService
import bob.colbaskin.iubip_spring2025.organizations.domain.remote.OrganizationsRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class OrganizationsRepositoryImpl @Inject constructor(
    private val organizationApiService: OrganizationApiService
): OrganizationsRepository {

    override suspend fun getAllOrganizations(): List<Organization> {
        delay(1500)
        return listOf(
            Organization("1", "ООО Ромашка", "Цветочный магазин"),
            Organization("2", "ИП Иванов", "Строительные услуги")
        )
    }
}