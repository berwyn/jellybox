package dev.berwyn.jellybox.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.berwyn.jellybox.ui.onboarding.onboardingRoutes
import org.jellyfin.sdk.Jellyfin

@Composable
fun JellyboxNavigation(
    jellyfin: Jellyfin,
    navGraphReady: @Composable (NavController) -> Unit = {},
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen()
        }

        onboardingRoutes(navController, jellyfin)
    }

    navGraphReady(navController)
}
