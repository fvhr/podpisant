package bob.colbaskin.iubip_spring2025.documents.data.models

import bob.colbaskin.iubip_spring2025.documents.domain.models.Document

fun DocumentDTO.toDomain() = Document(
    id = id,
    name = name,
    organizationId = organizationId,
    fileUrl = fileUrl,
    createdAt = createdAt,
    status = when(status.lowercase()) {
        "in_progress" -> Document.DocumentStatus.IN_PROGRESS
        "signed" -> Document.DocumentStatus.SIGNED
        "rejected" -> Document.DocumentStatus.REJECTED
        else -> throw IllegalArgumentException("Unknown status: $status")
    },
    type = when(type?.lowercase()) {
        "strict" -> Document.DocumentType.STRICT
        "regular" -> Document.DocumentType.REGULAR
        else -> null
    },
    creatorId = creatorId
)