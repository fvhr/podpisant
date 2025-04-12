package bob.colbaskin.iubip_spring2025.navigation

import bob.colbaskin.iubip_spring2025.organizations.domain.models.Organization
import kotlinx.serialization.Serializable

sealed interface Screens {
    @Serializable
    data object Organizations: Screens

    @Serializable
    data object Documents: Screens

    @Serializable
    data class Profile(val id: String): Screens

    @Serializable
    data object Welcome: Screens

    @Serializable
    data object Introduction: Screens

    @Serializable
    data object EmailInput: Screens

    @Serializable
    data object OTPScreen: Screens

    @Serializable
    data class OrganizationDetailed(val organizationId: String): Screens
}