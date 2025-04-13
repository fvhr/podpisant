package bob.colbaskin.iubip_spring2025.organizations.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrganizationResponse(
   @SerialName("oraganization_id") val organizationId: Int
)