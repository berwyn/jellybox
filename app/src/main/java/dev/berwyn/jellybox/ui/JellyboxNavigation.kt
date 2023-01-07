package dev.berwyn.jellybox.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.berwyn.jellybox.ui.onboarding.onboardingRoutes
import dev.berwyn.jellybox.ui.placeholder.PlaceholderScreen
import org.jellyfin.sdk.Jellyfin

@Composable
fun JellyboxNavigation(
    jellyfin: Jellyfin,
    navGraphReady: @Composable (NavController) -> Unit = {},
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "placeholder") {
        composable("placeholder") {
            PlaceholderScreen()
        }

        onboardingRoutes(navController, jellyfin)
    }

    navGraphReady(navController)
}