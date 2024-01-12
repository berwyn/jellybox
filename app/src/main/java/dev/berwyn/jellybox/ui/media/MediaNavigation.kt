package dev.berwyn.jellybox.ui.media

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import dev.berwyn.jellybox.ui.locals.LocalApplicationState

fun NavGraphBuilder.mediaRoutes(navController: NavHostController) {
    navigation(route = "media", startDestination = "library") {
        composable("library") {
            MediaScreen(
                LocalApplicationState.current,
                onItemSelected = {
                    navController.navigate("details/$it")
                },
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable(
            route = "details/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) {
            MediaItemScreen(
                onBackPressed = { navController.popBackStack() },
                onPlayPressed = { id -> navController.navigate("play/$id") }
            )
        }

        composable(
            route = "play/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) { backStack ->
            val itemId = backStack.arguments!!.getString("id")!!

            MediaPlayer(itemId = itemId)
        }
    }
}
