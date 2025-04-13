package bob.colbaskin.iubip_spring2025.documents.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DocumentDTO(
    val id: Int,
    val name: String,
    @SerialName("organization_id") val organizationId: Int,
    @SerialName("file_url") val fileUrl: String,
    @SerialName("created_at") val createdAt: String,
    val status: String,
    val type: String?,
    @SerialName("creator_id") val creatorId: String
)