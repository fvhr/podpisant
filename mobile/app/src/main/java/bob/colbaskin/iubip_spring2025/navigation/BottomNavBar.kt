package bob.colbaskin.iubip_spring2025.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import bob.colbaskin.iubip_spring2025.ui.theme.BottomBarColor
import bob.colbaskin.iubip_spring2025.ui.theme.CardColor
import bob.colbaskin.iubip_spring2025.ui.theme.TextColor

@Composable
fun BottomNavBar(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    NavigationBar(
        containerColor = BottomBarColor,
        contentColor = TextColor
    ) {
        Destinations.entries.forEach { destination ->
             val selected = currentDestination?.hierarchy?.any {
                 it.hasRoute(destination.screen::class)
             } == true
            NavigationBarItem (
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },
                label = { Text(text = destination.label) },
                selected = selected,
                onClick = {
                    navController.navigate(destination.screen) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TextColor,
                    selectedTextColor = TextColor,
                    unselectedIconColor = TextColor,
                    unselectedTextColor = TextColor,
                    indicatorColor = CardColor
                )
            )
        }
    }
}