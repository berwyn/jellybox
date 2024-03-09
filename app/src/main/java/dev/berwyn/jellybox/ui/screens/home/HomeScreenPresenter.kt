package dev.berwyn.jellybox.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.berwyn.jellybox.data.local.Server
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow

@Composable
fun homeScreenPresenter(
    serversFlow: Flow<ImmutableList<Server>>,
    activeServerFlow: Flow<Server?>,
    onboardingRequestedFlow: Flow<Boolean>
): HomeScreenModel.State {
    val servers by serversFlow.collectAsState(initial = persistentListOf())
    val activeServer by activeServerFlow.collectAsState(initial = null)
    val onboardingRequested by onboardingRequestedFlow.collectAsState(initial = false)

    return when {
        onboardingRequested || servers.isEmpty() -> HomeScreenModel.State.Onboarding
        else -> HomeScreenModel.State.Ready(servers, activeServer)
    }
}
