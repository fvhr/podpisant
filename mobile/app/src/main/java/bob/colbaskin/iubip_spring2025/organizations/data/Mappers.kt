package bob.colbaskin.iubip_spring2025.organizations.data

import bob.colbaskin.iubip_spring2025.organizations.data.models.DepartmentDTO
import bob.colbaskin.iubip_spring2025.organizations.data.models.DepartmentUserDTO
import bob.colbaskin.iubip_spring2025.organizations.data.models.OrganizationDTO
import bob.colbaskin.iubip_spring2025.organizations.domain.models.Organization

fun OrganizationDTO.toDomain() = Organization(
    id = id,
    name = name,
    description = description
)


fun DepartmentDTO.toDomain() = Department(
    id = id,
    name = name,
    description = description,
    users = users.map { it.toDomain() }
)

fun DepartmentUserDTO.toDomain() = DepartmentUser(
    id = id,
    fio = fio
)