package dev.berwyn.jellybox.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import cafe.adriel.lyricist.ProvideStrings
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.data.local.Jellybox
import dev.berwyn.jellybox.domain.CreateClientUseCase
import dev.berwyn.jellybox.ensureSession
import dev.berwyn.jellybox.ui.locals.JellyfinClientState
import dev.berwyn.jellybox.ui.locals.LocalJellyfinClientState
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.coroutines.Dispatchers
import org.koin.compose.koinInject

@Composable
fun JellyboxApp(
    jellybox: Jellybox = koinInject(),
    applicationState: ApplicationState = koinInject(),
    createClient: CreateClientUseCase = koinInject(),
) {
    val scope = rememberCoroutineScope()
    val selectedServerFlow = remember {
        jellybox.serverQueries
            .getSelectedServer()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
    }

    val jellyfinClientState by remember {
        scope.launchMolecule(RecompositionMode.ContextClock) {
            JellyfinClientPresenter(
                createClient = createClient,
                selectedServerFlow = selectedServerFlow,
            )
        }
    }.collectAsState()

    LaunchedEffect(jellyfinClientState) {
        jellyfinClientState.also { state ->
            if (state is JellyfinClientState.Configured) {
                applicationState.currentClient = state.client.also {
                    it.ensureSession()
                }
            }
        }
    }

    CompositionLocalProvider(LocalJellyfinClientState provides jellyfinClientState) {
        ProvideStrings {
            JellyboxTheme {
                JellyboxNavigation()
            }
        }
    }
}
