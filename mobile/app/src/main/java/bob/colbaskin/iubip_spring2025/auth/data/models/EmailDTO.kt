package bob.colbaskin.iubip_spring2025.auth.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EmailDTO(
    val email: String
)