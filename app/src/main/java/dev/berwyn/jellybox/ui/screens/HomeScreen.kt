package dev.berwyn.jellybox.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.koin.getScreenModel
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.data.JellyfinServerRepository
import dev.berwyn.jellybox.data.local.JellyfinServer
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
        TODO("Load create new server wizard")
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

                    if (model.activeServer != null) {
                        Text("Connected to ${model.activeServer!!.name}")
                    } else {
                        Text("Not connected")
                    }
                }

            }
        }
    }
}
