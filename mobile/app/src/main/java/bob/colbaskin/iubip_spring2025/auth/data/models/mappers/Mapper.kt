package bob.colbaskin.iubip_spring2025.auth.data.models.mappers

import bob.colbaskin.iubip_spring2025.auth.data.models.LoginResponseDTO
import bob.colbaskin.iubip_spring2025.auth.domain.models.LoginResponse

fun LoginResponseDTO.toDomain(): LoginResponse {
    return LoginResponse(
        deviceId = this.deviceId,
        status = this.status
    )
}

fun LoginResponse.toData(): LoginResponseDTO {
    return LoginResponseDTO(
        deviceId = this.deviceId,
        status = this.status
    )
}