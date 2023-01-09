package dev.berwyn.jellybox.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.berwyn.jellybox.ui.navigation.JellyboxNavBar
import dev.berwyn.jellybox.ui.navigation.JellyboxNavRail
import dev.berwyn.jellybox.ui.onboarding.onboardingRoutes
import dev.berwyn.jellybox.ui.previews.DevicePreview
import dev.berwyn.jellybox.ui.previews.ThemePreview
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import dev.berwyn.jellybox.ui.util.LocalWidthSizeClass

@Composable
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
fun JellyboxNavigation(
    navGraphReady: @Composable (NavController) -> Unit = {},
) {
    val navController = rememberNavController()
    val widthSizeClass = LocalWidthSizeClass.current

    Scaffold(
        bottomBar = {
            if (widthSizeClass == WindowWidthSizeClass.Compact) {
                JellyboxNavBar()
            }
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumedWindowInsets(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
        ) {
            if (widthSizeClass != WindowWidthSizeClass.Compact) {
                JellyboxNavRail()
            }

            Column(modifier = Modifier.fillMaxSize()) {
                NavHost(navController = navController, startDestination = "home", modifier = Modifier.weight(1f)) {
                    composable("home") {
                        HomeScreen()
                    }

                    onboardingRoutes(navController)
                }


            }
        }
    }

    navGraphReady(navController)
}

@Composable
@ThemePreview
@DevicePreview
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun JellyboxNavigationPreview() {
    JellyboxTheme {
        JellyboxNavigation()
    }
}
