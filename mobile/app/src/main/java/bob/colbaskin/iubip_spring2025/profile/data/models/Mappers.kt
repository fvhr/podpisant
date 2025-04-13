package bob.colbaskin.iubip_spring2025.profile.data.models

import bob.colbaskin.iubip_spring2025.organizations.data.toDomain
import bob.colbaskin.iubip_spring2025.profile.domain.models.NotificationType
import bob.colbaskin.iubip_spring2025.profile.domain.models.OrganizationTags
import bob.colbaskin.iubip_spring2025.profile.domain.models.Profile

fun ProfileDTO.toDomain() = Profile(
    id = id,
    email = email,
    fullName = fio,
    phone = phone,
    isSuperAdmin = isSuperAdmin,
    notificationType = when (notificationType) {
        "TG" -> NotificationType.TG
        "EMAIL" -> NotificationType.EMAIL
        "PHONE" -> NotificationType.PHONE
        else -> throw IllegalArgumentException("Unknown notification type")
    },
    adminOrganizationId = adminInOrganization,
    userOrganizations = userOrganizations.map { it.toDomain() },
    userDepartmentsIds = userDepartmentsIds,
    organizationTags = OrganizationTags()
)