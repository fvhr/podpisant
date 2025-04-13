package bob.colbaskin.iubip_spring2025.documents.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DocumentStage(
    val id: Int,
    val name: String,
    val number: Int,
    val deadline: String,
    @SerialName("is_current") val isCurrent: Boolean,
    @SerialName("created_at") val createdAt: String,
    @SerialName("signed_users") val signedUsers: List<DocumentUser>,
    @SerialName("unsigned_users") val unsignedUsers: List<DocumentUser>,
    @SerialName("is_completed") val isCompleted: Boolean
)

@Serializable
data class DocumentUser(
    @SerialName("user_id") val userId: String,
    val fio: String,
    val email: String,
    @SerialName("signed_at") val signedAt: String?,
    @SerialName("signature_type") val signatureType: String?
)
