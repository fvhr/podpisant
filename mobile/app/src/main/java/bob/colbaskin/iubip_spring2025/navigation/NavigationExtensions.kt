package bob.colbaskin.iubip_spring2025.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

inline fun <reified T> NavGraphBuilder.animatedTransition(
    noinline content: @Composable (backStackEntry: NavBackStackEntry) -> Unit
) where T : Any, T : Screens {
    composable<T>(
        enterTransition = {
            scaleIn(tween(300)) + fadeIn(tween(300))
        },
        exitTransition = {
            scaleOut(tween(300)) + fadeOut(tween(300))
        },
        popEnterTransition = {
            scaleIn(tween(300)) + fadeIn(tween(300))
        },
        popExitTransition = {
            scaleOut(tween(300)) + fadeOut(tween(300))
        }
    ) { backStackEntry ->
        content(backStackEntry)
    }
}