package bob.colbaskin.iubip_spring2025.auth.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CodeToTokenDTO(
    @SerialName("auth_code") val authCode: String,
    @SerialName("code_challenger") val codeChallenger: String
)