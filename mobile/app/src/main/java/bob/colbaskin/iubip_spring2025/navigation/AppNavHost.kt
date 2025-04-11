package bob.colbaskin.iubip_spring2025.navigation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import bob.colbaskin.iubip_spring2025.common.models.AuthConfig
import bob.colbaskin.iubip_spring2025.common.models.UiState
import bob.colbaskin.iubip_spring2025.navigation.graphs.Graphs
import bob.colbaskin.iubip_spring2025.navigation.graphs.mainGraph
import bob.colbaskin.iubip_spring2025.navigation.graphs.onBoardingGraph
import androidx.navigation.compose.currentBackStackEntryAsState
import bob.colbaskin.iubip_spring2025.ui.theme.BackgroundColor

@Composable
fun AppNavHost(uiState: UiState.Success) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()

    val currentDestination = currentBackStack?.destination?.route
    val isTrueScreen: Boolean = Destinations.entries.any { destination ->
        val screen = destination.screen
        val screenClassName = screen::class.simpleName
        val currentScreenName = currentDestination?.substringAfterLast(".") ?: ""
        when (screen) {
            is Screens.Profile -> currentScreenName.startsWith("Profile")
            else -> currentScreenName == screenClassName
        }
    }

    Scaffold(
        bottomBar = {
            Log.d("LOG", "destination: $currentDestination")
            Log.d("LOG", "trueScreen: $isTrueScreen")
            AnimatedVisibility(
                visible = isTrueScreen
            ) {
                BottomNavBar(navController)
            }
        },
        containerColor = BackgroundColor
    ) { innerPadding ->
        NavHost(
            startDestination = getStartDestination(uiState.data.authStatus),
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        ) {
            onBoardingGraph(
                navController,
                uiState.data.onBoardingStatus
            )
            mainGraph(navController)
        }
    }
}

private fun getStartDestination(status: AuthConfig) =
    when (status) {
        AuthConfig.AUTHENTICATED -> Graphs.Main
        else -> Graphs.OnBoarding
    }