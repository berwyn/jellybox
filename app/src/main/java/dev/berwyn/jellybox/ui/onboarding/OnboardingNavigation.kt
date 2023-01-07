package dev.berwyn.jellybox.ui.onboarding

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.jellyfin.sdk.Jellyfin

fun NavGraphBuilder.onboardingRoutes(controller: NavController, jellyfin: Jellyfin) {
    composable("onboarding") {
        OnboardingScreen()
    }
}