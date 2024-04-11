package dev.berwyn.jellybox.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.berwyn.jellybox.data.local.Server
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.take

@Composable
fun HomeScreenPresenter(
    serversFlow: Flow<ImmutableList<Server>>,
    activeServerFlow: Flow<Server?>,
    onboardingRequestedFlow: Flow<Boolean>,
): HomeScreenModel.State {
    val isLoading by remember {
        combine(serversFlow, activeServerFlow) { _, _ -> false }
            .take(1)
    }.collectAsState(initial = true)

    val servers by serversFlow.collectAsState(initial = persistentListOf())
    val activeServer by activeServerFlow.collectAsState(initial = null)

    val onboardingRequested by onboardingRequestedFlow.collectAsState(initial = false)

    return when {
        isLoading -> HomeScreenModel.State.Loading
        onboardingRequested || servers.isEmpty() -> HomeScreenModel.State.Onboarding
        activeServer == null -> HomeScreenModel.State.SelectingServer(servers)
        else -> HomeScreenModel.State.Ready(
            servers,
            activeServer!!,
        )
    }
}
