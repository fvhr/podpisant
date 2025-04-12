package bob.colbaskin.iubip_spring2025.documents.data.models

import bob.colbaskin.iubip_spring2025.documents.domain.models.DocumentStatus
import kotlinx.serialization.Serializable

@Serializable
data class DocumentDTO(
    val id: String,
    val title: String,
    val creationDate: String,
    val author: String,
    val status: DocumentStatus,
    val fileUrl: String? = null
)
