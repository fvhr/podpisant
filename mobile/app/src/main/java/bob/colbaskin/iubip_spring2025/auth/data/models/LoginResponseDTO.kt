package bob.colbaskin.iubip_spring2025.auth.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDTO(
    @SerialName("device_id") val deviceId: String,
    val status: String
)