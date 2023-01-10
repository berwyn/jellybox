package dev.berwyn.jellybox.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import dev.berwyn.jellybox.data.local.JellyfinServer
import dev.berwyn.jellybox.domain.SelectActiveServerUseCase
import dev.berwyn.jellybox.ui.previews.ThemePreview
import kotlinx.coroutines.launch

@Composable
fun JellyboxNavRail(
    servers: List<JellyfinServer>,
    selectActiveServer: SelectActiveServerUseCase,
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    NavigationRail(
        modifier = modifier,
        header = {
            ServerSelectionMenu(
                servers = servers,
                onServerSelected = { server ->
                    coroutineScope.launch {
                        selectActiveServer(server)
                    }
                },
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
                label = { Text(stringResource(destination.titleTextId)) }
            )
        }
    }
}
