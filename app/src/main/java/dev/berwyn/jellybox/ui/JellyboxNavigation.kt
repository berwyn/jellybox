package dev.berwyn.jellybox.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.berwyn.jellybox.data.local.JellyfinServer
import dev.berwyn.jellybox.domain.SelectActiveServerUseCase
import dev.berwyn.jellybox.ui.navigation.*
import dev.berwyn.jellybox.ui.onboarding.onboardingRoutes
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
fun JellyboxNavigation(
    servers: List<JellyfinServer>,
    selectActiveServer: SelectActiveServerUseCase,
    navigationType: NavigationType,
    navigationHidden: Boolean,
    navigationState: NavigationState = rememberNavigationState(),
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            if (navigationType == NavigationType.Bar && !navigationHidden) {
                JellyboxNavBar(
                    destinations = navigationState.topLevelDestinations,
                    onNavigateToDestination = navigationState::navigateToTopLevelDestination,
                    currentDestination = navigationState.currentDestination,
                )
            }
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
        ) {
            if (navigationType == NavigationType.Rail && !navigationHidden) {
                JellyboxNavRail(
                    servers = servers,
                    selectActiveServer = { server ->
                        coroutineScope.launch {
                            selectActiveServer(server)
                        }
                    },
                    destinations = navigationState.topLevelDestinations,
                    onNavigateToDestination = navigationState::navigateToTopLevelDestination,
                    currentDestination = navigationState.currentDestination,
                )
            }

            Column(modifier = Modifier.fillMaxSize()) {
                navigationState.currentTopLevelDestination?.let { destination ->
                    TopAppBar(
                        title = { Text(stringResource(destination.titleTextId)) },
                        navigationIcon = {
                            ServerSelectionMenu(
                                servers = servers,
                                onServerSelected = { server ->
                                    navigationState.coroutineScope.launch {
                                        selectActiveServer(server)
                                    }
                                },
                            )
                        }
                    )
                }

                NavHost(
                    navController = navigationState.navController,
                    startDestination = "home",
                    modifier = Modifier.weight(1f)
                ) {
                    composable("home") {
                        HomeScreen(
                            onOnboardingRequested = navigationState::goToOnboarding
                        )
                    }

                    onboardingRoutes(navigationState.navController)
                }
            }
        }
    }
}
