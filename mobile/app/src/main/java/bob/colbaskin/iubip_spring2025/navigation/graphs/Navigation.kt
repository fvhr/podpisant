package bob.colbaskin.iubip_spring2025.navigation.graphs

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import bob.colbaskin.iubip_spring2025.auth.presentation.EmailInputScreen
import bob.colbaskin.iubip_spring2025.auth.presentation.otp.OtpScreen
import bob.colbaskin.iubip_spring2025.common.models.OnBoardingConfig
import bob.colbaskin.iubip_spring2025.documents.presentation.DocumentDetailedScreen
import bob.colbaskin.iubip_spring2025.documents.presentation.DocumentsScreen
import bob.colbaskin.iubip_spring2025.navigation.Screens
import bob.colbaskin.iubip_spring2025.navigation.animatedTransition
import bob.colbaskin.iubip_spring2025.onboarding.presentation.IntroductionScreen
import bob.colbaskin.iubip_spring2025.onboarding.presentation.WelcomeScreen
import bob.colbaskin.iubip_spring2025.organizations.presentation.OrganizationDetailedScreen
import bob.colbaskin.iubip_spring2025.organizations.presentation.OrganizationsScreen

fun NavGraphBuilder.onBoardingGraph(
    navController: NavHostController,
    onBoardingStatus: OnBoardingConfig
) {
    navigation<Graphs.OnBoarding>(
        startDestination = getStartDestination(onBoardingStatus)
    ) {
        animatedTransition<Screens.Welcome> {
            WelcomeScreen (
                onNextScreen = { navController.navigate(Screens.Introduction) {
                    popUpTo(Screens.Welcome) { inclusive = true }
                }}
            )
        }
        animatedTransition<Screens.Introduction> {
            IntroductionScreen (
                onNextScreen = { navController.navigate(Screens.EmailInput) {
                    popUpTo(Screens.Introduction) { inclusive = true }
                }}
            )
        }
        animatedTransition<Screens.EmailInput> {
            EmailInputScreen(
                onNextScreen = { navController.navigate(Screens.OTPScreen) },
                onError = { navController.navigate(Screens.EmailInput) {
                    popUpTo(Screens.EmailInput) { inclusive = true}
                }}
            )
        }
        animatedTransition<Screens.OTPScreen> {
            OtpScreen(
                onNextScreen = { navController.navigate(Screens.Organizations) {
                    popUpTo(Screens.OTPScreen) { inclusive = true }
                    popUpTo(Screens.EmailInput) { inclusive = true }
                }}
            )
        }
    }
}

fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    navigation<Graphs.Main>(
        startDestination = Screens.Organizations
    ) {
        animatedTransition<Screens.Organizations> {
            OrganizationsScreen(
                onNavigateToOrganizationDetails = { orgId ->
                    navController.navigate(Screens.OrganizationDetailed(orgId))
                }
            )
        }
        animatedTransition<Screens.OrganizationDetailed> { backStackEntry ->
            val detailedOrganization: Screens.OrganizationDetailed = backStackEntry.toRoute()
            OrganizationDetailedScreen(
                organizationId = detailedOrganization.organizationId,
                onBack = { navController.navigateUp() }
            )
        }
        animatedTransition<Screens.Documents> {
            DocumentsScreen(
                onDocumentClick = {documentId ->
                    navController.navigate(Screens.DocumentDetailed(documentId))
                }
            )
        }
        animatedTransition<Screens.DocumentDetailed> { backStackEntry ->
            val documentDetailed: Screens.DocumentDetailed = backStackEntry.toRoute()
            DocumentDetailedScreen(
                documentId = documentDetailed.documentId,
                onBack = { navController.navigateUp() }
            )
        }
        animatedTransition<Screens.Profile> { backStackEntry ->
            val profile: Screens.Profile = backStackEntry.toRoute()
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(profile.id)
            }
        }
    }
}


private fun getStartDestination(status: OnBoardingConfig) = when (status) {
    OnBoardingConfig.NOT_STARTED -> Screens.Welcome
    OnBoardingConfig.IN_PROGRESS -> Screens.Introduction
    OnBoardingConfig.COMPLETED -> Screens.EmailInput
    else -> Screens.Welcome
}