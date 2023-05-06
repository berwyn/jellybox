package dev.berwyn.jellybox.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
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
import dev.berwyn.jellybox.ui.media.mediaRoutes
import dev.berwyn.jellybox.ui.navigation.JellyboxNavBar
import dev.berwyn.jellybox.ui.navigation.JellyboxNavRail
import dev.berwyn.jellybox.ui.navigation.NavigationState
import dev.berwyn.jellybox.ui.navigation.NavigationType
import dev.berwyn.jellybox.ui.navigation.ServerSelectionMenu
import dev.berwyn.jellybox.ui.navigation.rememberNavigationState
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
            AnimatedVisibility(
                visible = navigationType == NavigationType.Bar && !navigationHidden,
                enter = slideInVertically() + fadeIn(),
                exit = fadeOut() + slideOutVertically(),
            ) {
                JellyboxNavBar(
                    destinations = enumValues(),
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
            AnimatedVisibility(
                visible = navigationType == NavigationType.Rail && !navigationHidden,
                enter = slideInHorizontally() + fadeIn(),
                exit = fadeOut() + slideOutHorizontally(),
            ) {
                JellyboxNavRail(
                    servers = servers,
                    selectActiveServer = { server ->
                        coroutineScope.launch {
                            selectActiveServer(server)
                        }
                    },
                    destinations = enumValues(),
                    onNavigateToDestination = navigationState::navigateToTopLevelDestination,
                    currentDestination = navigationState.currentDestination,
                )
            }

            Column(modifier = Modifier.fillMaxSize()) {
                navigationState.currentTopLevelDestination?.let { destination ->
                    TopAppBar(
                        title = { Text(stringResource(destination.titleTextId)) },
                        navigationIcon = {
                            AnimatedVisibility(visible = navigationType == NavigationType.Bar) {
                                ServerSelectionMenu(
                                    servers = servers,
                                    onServerSelected = { server ->
                                        navigationState.coroutineScope.launch {
                                            selectActiveServer(server)
                                        }
                                    },
                                )
                            }
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
                    mediaRoutes(navigationState.navController)
                }
            }
        }
    }
}
