package bob.colbaskin.iubip_spring2025.organizations.data.models

import kotlinx.serialization.Serializable

@Serializable
data class OrganizationDTO(
    val id: Int,
    val name: String,
    val description: String
)