package bob.colbaskin.iubip_spring2025.navigation

import kotlinx.serialization.Serializable

sealed interface Screens {
    @Serializable
    data object Organizations: Screens

    @Serializable
    data class OrganizationDetailed(val organizationId: Int): Screens

    @Serializable
    data object Documents: Screens

    @Serializable
    data class DocumentDetailed(val documentId: Int): Screens

    @Serializable
    data object Profile: Screens

    @Serializable
    data object Welcome: Screens

    @Serializable
    data object Introduction: Screens

    @Serializable
    data object EmailInput: Screens

    @Serializable
    data object OTPScreen: Screens
}