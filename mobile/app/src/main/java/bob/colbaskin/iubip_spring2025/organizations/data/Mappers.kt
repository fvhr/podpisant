package bob.colbaskin.iubip_spring2025.organizations.data

import bob.colbaskin.iubip_spring2025.organizations.data.models.OrganizationDTO
import bob.colbaskin.iubip_spring2025.organizations.domain.models.Organization

fun OrganizationDTO.toDomain() = Organization(
    id = id,
    name = name,
    description = description
)