package dev.berwyn.jellybox.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.tracing.trace
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberNavigationState(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): NavigationState {
    return remember(navController, coroutineScope) {
        NavigationState(navController = navController, coroutineScope = coroutineScope)
    }
}

class NavigationState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState()
            .value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when(currentDestination?.route) {
            "home" -> TopLevelDestination.HOME
            else -> null
        }

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().toList()

    fun goToOnboarding() {
        trace("Navigation: onboarding") {
            navController.navigate("onboarding") {
                popUpTo(0)
            }
        }
    }

    fun navigateToTopLevelDestination(destination: TopLevelDestination) {
        trace("Navigation: ${destination.name}") {
            val topLevelNavOptions = navOptions {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }

                launchSingleTop = true
                restoreState = true
            }

            navController.navigate(destination.route)
        }
    }
}
