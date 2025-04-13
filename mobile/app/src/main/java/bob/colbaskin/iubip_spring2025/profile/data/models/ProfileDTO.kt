package bob.colbaskin.iubip_spring2025.profile.data.models

import bob.colbaskin.iubip_spring2025.organizations.data.models.OrganizationDTO
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ProfileDTO(
    @SerialName("admin_in_organization") val adminInOrganization: Int,
    val email: String,
    val fio: String,
    val id: String,
    @SerialName("is_super_admin")
    val isSuperAdmin: Boolean,

    @SerialName("organization_tags")
    val organizationTags: OrganizationTags,

    val phone: String,

    @SerialName("type_notification")
    val notificationType: String,

    @SerialName("user_departments_ids")
    val userDepartmentsIds: List<Int>,

    @SerialName("user_organizations")
    val userOrganizations: List<OrganizationDTO>
) {
    @Serializable
    class OrganizationTags()
}