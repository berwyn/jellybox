package dev.berwyn.jellybox.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.berwyn.jellybox.data.local.Server
import dev.berwyn.jellybox.domain.CreateClientUseCase
import dev.berwyn.jellybox.ui.locals.JellyfinClientState
import kotlinx.coroutines.flow.Flow

@Composable
fun JellyfinClientPresenter(
    createClient: CreateClientUseCase,
    selectedServerFlow: Flow<Server?>
): JellyfinClientState {
    val selectedServer by selectedServerFlow.collectAsState(initial = null)

    return selectedServer?.let {  server ->
        JellyfinClientState.Configured(createClient(server))
    } ?: JellyfinClientState.Unset
}
