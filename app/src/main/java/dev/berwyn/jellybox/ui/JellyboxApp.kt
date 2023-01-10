package dev.berwyn.jellybox.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.berwyn.jellybox.data.local.JellyfinServer
import dev.berwyn.jellybox.domain.SelectActiveServerUseCase
import dev.berwyn.jellybox.ui.navigation.NavigationType
import dev.berwyn.jellybox.ui.navigation.rememberNavigationState
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Composable
fun JellyboxApp(
    navigationType: NavigationType,
    hasServersConfigured: Flow<Boolean>,
    savedServers: Flow<List<JellyfinServer>>,
    selectActiveServer: SelectActiveServerUseCase,
) {
    val servers by savedServers.collectAsState(initial = listOf())

    JellyboxTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navState = rememberNavigationState()

            JellyboxNavigation(
                navigationType = navigationType,
                navigationState = navState,
                servers = servers,
                selectActiveServer = selectActiveServer,
            )

            LaunchedEffect(navState) {
                val hasServers = hasServersConfigured.first()

                if (!hasServers) {
                    navState.goToOnboarding()
                } else {
                    selectActiveServer(useDefault = true)
                }
            }
        }
    }
}
