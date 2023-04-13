package dev.berwyn.jellybox.ui.onboarding

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.onboardingRoutes(controller: NavController) {
    composable("onboarding") {
        OnboardingScreen(
            onSetupComplete = {
                controller.navigate("home") {
                    launchSingleTop = true
                    popUpTo(0)
                }
            }
        )
    }
}
