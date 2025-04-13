package bob.colbaskin.iubip_spring2025.organizations.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DepartmentDTO(
    val id: Int,
    val name: String,
    val description: String,
    @SerialName("created_at") val createdAt: String,
    val users: List<DepartmentUserDTO>
)

@Serializable
data class DepartmentUserDTO(
    val id: String,
    val fio: String
)
