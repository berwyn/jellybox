package dev.berwyn.jellybox.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.koin.getScreenModel
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.data.JellyfinServerRepository
import dev.berwyn.jellybox.data.local.JellyfinServer
import dev.berwyn.jellybox.ui.components.FullscreenDialog
import dev.berwyn.jellybox.ui.onboarding.OnboardingScreen
import dev.berwyn.jellybox.ui.screens.home.HomeScreenAppbar
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

data class HomeScreenModel(
    private val appState: ApplicationState,
    private val serverRepository: JellyfinServerRepository,
) : ScreenModel {
    val activeServer: JellyfinServer?
        get() = appState.selectedServer

    val savedServers = mutableStateListOf<JellyfinServer>()

    var onboardingVisible = mutableStateOf(false)
        private set

    private var flowJob: Job? = null

    init {
        flowJob = screenModelScope.launch {
            appState.ensureSession()

            serverRepository.getServers()
                .collect { servers ->
                    savedServers.clear()
                    savedServers.addAll(servers)
                }
        }
    }

    fun selectServer(server: JellyfinServer) {
        appState.selectServer(server)
    }

    fun addNewServer() {
        onboardingVisible.value = true
    }

    fun cancelOnboarding() {
        onboardingVisible.value = false
    }

    fun completeOnboarding() {
        onboardingVisible.value = false
    }

    override fun onDispose() {
        flowJob?.cancel()
        super.onDispose()
    }
}

@Parcelize
class HomeScreen : ParcelableScreen {
    @Composable
    override fun Content() {
        val model = getScreenModel<HomeScreenModel>()
        val onboardingVisible by model.onboardingVisible

        if (onboardingVisible) {
            FullscreenDialog(onDismissRequest = model::cancelOnboarding) {
                OnboardingScreen(onSetupComplete = model::completeOnboarding)
            }
        }

        Scaffold(
            topBar = {
                HomeScreenAppbar(
                    selectedServer = model.activeServer,
                    savedServers = model.savedServers.toImmutableList(),
                    onServerSelected = model::selectServer,
                    onCreateNewServer = model::addNewServer,
                )
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding), contentAlignment = Alignment.Center) {
                Column {
                    Text("Home screen")

                    model.activeServer?.let {
                        Text("Connected to ${it.name}")
                    }
                }

            }
        }
    }
}
