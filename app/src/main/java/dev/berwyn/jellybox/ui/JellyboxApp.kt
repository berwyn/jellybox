package dev.berwyn.jellybox.ui

import androidx.compose.runtime.Composable
import dev.berwyn.jellybox.ui.locals.LocalApplicationState
import dev.berwyn.jellybox.ui.navigation.rememberNavigationState
import dev.berwyn.jellybox.ui.theme.JellyboxTheme

@Composable
fun JellyboxApp() {
    JellyboxTheme {
        val navState = rememberNavigationState()
        val appState = LocalApplicationState.current

        JellyboxNavigation(
            navigationState = navState,
            navigationHidden = appState.navigationHidden,
            showBottomSheet = appState.selectedMediaItem != null,
            allowBottomSheetSwipe = appState.isVideoSelected.not(),
        )
    }
}
