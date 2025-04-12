package bob.colbaskin.iubip_spring2025.documents.domain.models

data class Document(
    val id: String,
    val title: String,
    val creationDate: String,
    val author: String,
    val status: DocumentStatus,
    val fileUrl: String? = null
)