package bob.colbaskin.iubip_spring2025.documents.domain.models

import androidx.compose.ui.graphics.Color

data class Document(
    val id: Int,
    val name: String,
    val organizationId: Int,
    val fileUrl: String,
    val createdAt: String,
    val status: DocumentStatus,
    val type: DocumentType? = null,
    val creatorId: String
) {
    enum class DocumentStatus(val color: Color) {
        ALL(Color(0xFF403C88)),
        REJECTED(Color(0xFFFF6265)),
        IN_PROGRESS(Color(0xFFFFB162)),
        SIGNED(Color(0xFFB1FF62))
    }
    enum class DocumentType { STRICT, REGULAR }
}