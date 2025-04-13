package bob.colbaskin.iubip_spring2025.organizations.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrgBody(
    val name: String,
    val description: String,
    @SerialName("admin_id") val adminId: String
)