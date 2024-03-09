package dev.berwyn.jellybox.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.getScreenModel
import dev.berwyn.jellybox.ui.onboarding.OnboardingScreen
import dev.berwyn.jellybox.ui.screens.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class HomeScreen : ParcelableScreen {
    @Composable
    override fun Content() {
        val model = getScreenModel<HomeScreenModel>()
        val state by model.state.collectAsState()

        Crossfade(state, label = "Home Screen") {
            when (it) {
                is HomeScreenModel.State.Onboarding -> {
                    BackHandler {
                        model.cancelOnboarding()
                    }

                    OnboardingScreen(onSetupComplete = model::completeOnboarding)
                }

                is HomeScreenModel.State.Ready -> {
                    val (servers, activeServer) = it

                    Scaffold(
                        topBar = {
                            HomeScreenAppbar(
                                selectedServer = activeServer,
                                savedServers = servers,
                                onServerSelected = model::selectServer,
                                onCreateNewServer = model::addNewServer,
                            )
                        }
                    ) { contentPadding ->
                        Box(modifier = Modifier.padding(contentPadding), contentAlignment = Alignment.Center) {
                            Column {
                                Text("Home screen")

                                activeServer?.let {
                                    Text("Connected to ${it.name}")
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
