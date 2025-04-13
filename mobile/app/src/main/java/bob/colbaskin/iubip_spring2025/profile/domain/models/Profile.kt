package bob.colbaskin.iubip_spring2025.profile.domain.models

import bob.colbaskin.iubip_spring2025.organizations.domain.models.Organization

data class Profile(
    val id: String,
    val email: String,
    val fullName: String,
    val phone: String,
    val isSuperAdmin: Boolean,
    val notificationType: NotificationType,
    val adminOrganizationId: List<Int>,
    val userOrganizations:  List<Organization> = emptyList(),
    val userDepartmentsIds: List<Int>,
    val organizationTags: OrganizationTags = OrganizationTags()
)

enum class NotificationType { TG, EMAIL, PHONE }

class OrganizationTags