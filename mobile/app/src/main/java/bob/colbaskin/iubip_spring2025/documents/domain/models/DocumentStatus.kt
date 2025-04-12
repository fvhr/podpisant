package bob.colbaskin.iubip_spring2025.documents.domain.models

import androidx.compose.ui.graphics.Color

enum class DocumentStatus(val color: Color) {
    ALL(Color(0xFF403C88)),
    UNSIGNED(Color(0xFFFF6265)),
    VERIFICATION(Color(0xFFFFB162)),
    SIGNED(Color(0xFFB1FF62))
}