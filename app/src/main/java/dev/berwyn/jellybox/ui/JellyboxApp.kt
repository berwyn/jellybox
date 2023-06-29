package dev.berwyn.jellybox.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.berwyn.jellybox.ui.navigation.NavigationType
import dev.berwyn.jellybox.ui.navigation.rememberNavigationState
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.collections.immutable.persistentListOf
import org.koin.compose.koinInject

@Composable
fun JellyboxApp(
    navigationType: NavigationType,
    navigationHidden: Boolean,
    viewModel: JellyboxAppViewModel = koinInject()
) {
    val servers by viewModel.servers.collectAsState(initial = persistentListOf())

    JellyboxTheme {
        val navState = rememberNavigationState()

        JellyboxNavigation(
            navigationType = navigationType,
            navigationState = navState,
            navigationHidden = navigationHidden,
            servers = servers,
            selectActiveServer = viewModel.selectActiveServer,
        )
    }
}
