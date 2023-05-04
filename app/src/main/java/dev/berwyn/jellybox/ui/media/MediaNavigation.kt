package dev.berwyn.jellybox.ui.media

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import dev.berwyn.jellybox.ui.locals.LocalApplicationState

fun NavGraphBuilder.mediaRoutes() {
    navigation(route = "media", startDestination = "library") {
        composable("library") {
            MediaScreen(LocalApplicationState.current)
        }
    }
}
