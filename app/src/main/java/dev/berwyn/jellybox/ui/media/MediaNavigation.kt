package dev.berwyn.jellybox.ui.media

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import dev.berwyn.jellybox.ui.locals.LocalApplicationState
import java.util.UUID

fun NavGraphBuilder.mediaRoutes(navController: NavHostController) {
    navigation(route = "media", startDestination = "library") {
        composable("library") {
            MediaScreen(
                LocalApplicationState.current,
                onItemSelected = {
                    navController.navigate("details/${it.toString()}") {

                    }
                }
            )
        }

        composable(
            route = "details/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments!!.getString("id")

            MediaItemScreen(
                itemId = UUID.fromString(id)
            )
        }
    }
}
