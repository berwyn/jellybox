package dev.berwyn.jellybox.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.skydoves.orbital.Orbital
import com.skydoves.orbital.rememberContentWithOrbitalScope
import dev.berwyn.jellybox.ui.media.mediaRoutes
import dev.berwyn.jellybox.ui.navigation.NavigationState
import dev.berwyn.jellybox.ui.navigation.TopLevelDestination
import dev.berwyn.jellybox.ui.navigation.isTopLevelDestinationInHierarchy
import dev.berwyn.jellybox.ui.navigation.rememberNavigationState
import dev.berwyn.jellybox.ui.onboarding.onboardingRoutes
import dev.berwyn.jellybox.ui.previews.DevicePreviews
import dev.berwyn.jellybox.ui.previews.DynamicColourPreviews
import dev.berwyn.jellybox.ui.previews.PreviewReplacer
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.theme.JellyboxTheme

@Composable
@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class, ExperimentalMaterial3Api::class)
fun JellyboxNavigation(
    navigationState: NavigationState = rememberNavigationState(),
    navigationHidden: Boolean = false,
    showBottomSheet: Boolean = false,
    allowBottomSheetSwipe: Boolean = true,
) {
    val currentDestination = navigationState.currentDestination

    val content = rememberContentWithOrbitalScope {
        PreviewReplacer(
            title = "Nav Host",
        ) {
            NavHost(
                navController = navigationState.navController,
                startDestination = "home",
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

    Orbital {
        if (navigationHidden) {
            Scaffold { contentPadding ->
                Box(modifier = Modifier.padding(contentPadding)) {
                    content()
                }
            }
        } else {
            NavigationSuiteScaffold(
                navigationSuiteItems = {
                    enumValues<TopLevelDestination>().forEach { destination ->
                        item(
                            icon = {
                                Icon(
                                    destination.unselectedIcon,
                                    contentDescription = stringResource(destination.iconTextId)
                                )
                            },
                            selected = currentDestination?.isTopLevelDestinationInHierarchy(destination) ?: false,
                            onClick = {
                                navigationState.navigateToTopLevelDestination(destination)
                            }
                        )
                    }
                }
            ) {
                val peekHeight = if (showBottomSheet) 56.dp else 0.dp

                BottomSheetScaffold(
                    sheetShape = RectangleShape,
                    sheetDragHandle = null,
                    sheetSwipeEnabled = allowBottomSheetSwipe,
                    sheetPeekHeight = peekHeight,
                    sheetContent = {
                        Text("Bottom Sheet")
                    },
                ) { contentPadding ->
                    Box(modifier = Modifier.padding(contentPadding)) {
                        content()
                    }
                }
            }
        }
    }


}

@Composable
@ThemePreviews
@DevicePreviews
@DynamicColourPreviews
private fun JellyboxNavigationPreview() {
    JellyboxTheme {
        JellyboxNavigation()
    }
}
