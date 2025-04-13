package bob.colbaskin.iubip_spring2025.organizations.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("user_organizations")
    val userOrganizations: List<OrganizationDTO>
)
