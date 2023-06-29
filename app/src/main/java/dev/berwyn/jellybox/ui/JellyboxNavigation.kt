package dev.berwyn.jellybox.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.berwyn.jellybox.data.local.JellyfinServer
import dev.berwyn.jellybox.domain.SelectActiveServerUseCase
import dev.berwyn.jellybox.ui.media.mediaRoutes
import dev.berwyn.jellybox.ui.navigation.JellyboxNavBar
import dev.berwyn.jellybox.ui.navigation.JellyboxNavRail
import dev.berwyn.jellybox.ui.navigation.NavigationState
import dev.berwyn.jellybox.ui.navigation.NavigationType
import dev.berwyn.jellybox.ui.navigation.rememberNavigationState
import dev.berwyn.jellybox.ui.onboarding.onboardingRoutes
import dev.berwyn.jellybox.ui.previews.PreviewNavigationTypeProvider
import dev.berwyn.jellybox.ui.previews.PreviewReplacer
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Composable
fun JellyboxNavigation(
    servers: ImmutableList<JellyfinServer>,
    selectActiveServer: SelectActiveServerUseCase,
    navigationType: NavigationType,
    navigationHidden: Boolean,
    navigationState: NavigationState = rememberNavigationState(),
) {
    val coroutineScope = rememberCoroutineScope()

    val showNavRail = remember(navigationType, navigationHidden) {
        navigationType == NavigationType.Rail && !navigationHidden
    }

    val showNavBar = remember(navigationType, navigationHidden) {
        navigationType == NavigationType.Bar && !navigationHidden
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showNavBar,
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
        ) {
            AnimatedVisibility(
                visible = showNavRail,
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
                val contentModifier = Modifier.weight(1f, fill = true).fillMaxWidth()

                PreviewReplacer(title = "Nav Host", modifier = contentModifier) {
                    NavHost(
                        navController = navigationState.navController,
                        startDestination = "home",
                        modifier = contentModifier,
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
}

@Preview
@Composable
private fun JellyboxNavigationPreview(
    @PreviewParameter(PreviewNavigationTypeProvider::class) navigationType: NavigationType
) {
    JellyboxTheme {
        JellyboxNavigation(
            servers = persistentListOf(),
            selectActiveServer = object : SelectActiveServerUseCase {
                override suspend fun invoke(server: JellyfinServer?, useDefault: Boolean) {
                    TODO("Not yet implemented")
                }
            },
            navigationType = navigationType,
            navigationHidden = false,
        )
    }
}
