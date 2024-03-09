package dev.berwyn.jellybox.ui.screens.home

import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.data.JellyfinServerRepository
import dev.berwyn.jellybox.data.local.Server
import dev.berwyn.jellybox.domain.SelectActiveServerUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeScreenModel(
    private val appState: ApplicationState,
    private val selectActiveServer: SelectActiveServerUseCase,
    private val serverRepository: JellyfinServerRepository,
) : ScreenModel {

    sealed class State {
        data object Onboarding : State()

        data class SelectingServer(
            val servers: ImmutableList<Server>
        ) : State()

        data class Ready(
            val servers: ImmutableList<Server>,
            val activeServer: Server?
        ) : State()
    }

    private val _onboardingRequestedFlow = MutableSharedFlow<Boolean>(1)
    val state: StateFlow<State> = screenModelScope.launchMolecule(mode = RecompositionMode.Immediate) {
        homeScreenPresenter(
            serversFlow = serverRepository.getServers(),
            activeServerFlow = serverRepository.getSelectedServer(),
            onboardingRequestedFlow = _onboardingRequestedFlow,
        )
    }

    fun selectServer(server: Server) {
        screenModelScope.launch {
            selectActiveServer(server)
        }
    }

    fun disconnectServer() {
        screenModelScope.launch {
            selectActiveServer(null)
        }
    }

    fun addNewServer() {
        _onboardingRequestedFlow.tryEmit(true)
    }

    fun cancelOnboarding() {
        _onboardingRequestedFlow.tryEmit(false)
    }

    fun completeOnboarding() {
        _onboardingRequestedFlow.tryEmit(false)
    }
}
