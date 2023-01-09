package dev.berwyn.jellybox.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.berwyn.jellybox.ui.previews.ThemePreview

@Composable
@ThemePreview
fun JellyboxNavRail() {
    NavigationRail {
        TopLevelDestination.values().forEach { route ->
            NavigationRailItem(
                selected = false,
                onClick = { },
                icon = {
                    val icon = if (false) route.selectedIcon else route.unselectedIcon

                    Icon(icon, stringResource(route.iconTextId))
                },
                label = { Text(stringResource(route.titleTextId)) }
            )
        }
    }
}
