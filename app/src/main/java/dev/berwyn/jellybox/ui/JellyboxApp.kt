package dev.berwyn.jellybox.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Composable
fun JellyboxApp(
    hasServersConfigured: Flow<Boolean>,
) {
    JellyboxTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            JellyboxNavigation { nav ->
                LaunchedEffect(nav) {
                    val hasServers = hasServersConfigured.first()

                    if (!hasServers) {
                        nav.navigate("onboarding") {
                            popUpTo(0)
                        }
                    }
                }
            }
        }
    }
}
