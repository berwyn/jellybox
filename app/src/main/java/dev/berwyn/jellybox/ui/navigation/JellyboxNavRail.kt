package dev.berwyn.jellybox.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import dev.berwyn.jellybox.data.local.JellyfinServer
import dev.berwyn.jellybox.ui.previews.DynamicColourPreviews
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun JellyboxNavRail(
    servers: ImmutableList<JellyfinServer>,
    selectActiveServer: (JellyfinServer) -> Unit,
    destinations: Array<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    NavigationRail(
        modifier = modifier,
        header = {
            ServerSelectionMenu(
                servers = servers,
                onServerSelected = selectActiveServer,
            )
        }
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)

            NavigationRailItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    val icon = if (selected) {
                        destination.selectedIcon
                    } else {
                        destination.unselectedIcon
                    }

                    Icon(icon, stringResource(destination.iconTextId))
                },
                label = {
                    if (selected) {
                        Text(stringResource(destination.titleTextId))
                    }
                }
            )
        }
    }
}

@Composable
@ThemePreviews
@DynamicColourPreviews
private fun JellyboxNavRailPreview() {
    JellyboxTheme {
        JellyboxNavRail(
            servers = persistentListOf(),
            selectActiveServer = {},
            destinations = enumValues<TopLevelDestination>(),
            onNavigateToDestination = {},
            currentDestination = null,
        )
    }
}
