package bob.colbaskin.iubip_spring2025.navigation.graphs

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
import bob.colbaskin.iubip_spring2025.navigation.Screens
import bob.colbaskin.iubip_spring2025.navigation.animatedTransition
import bob.colbaskin.iubip_spring2025.onboarding.presentation.IntroductionScreen
import bob.colbaskin.iubip_spring2025.onboarding.presentation.WelcomeScreen
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
                onNextScreen = { navController.navigate(Screens.OTPScreen)}
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
            OrganizationsScreen()
        }
        animatedTransition<Screens.Documents> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Some Screen")
            }
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