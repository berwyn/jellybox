package dev.berwyn.jellybox.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigatorSaver
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.parcelableNavigatorSaver
import dev.berwyn.jellybox.ui.previews.DevicePreviews
import dev.berwyn.jellybox.ui.previews.DynamicColourPreviews
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.screens.HomeScreen
import dev.berwyn.jellybox.ui.theme.JellyboxTheme

@Composable
@OptIn(ExperimentalVoyagerApi::class)
fun JellyboxNavigation() {
    BottomSheetNavigator {
        CompositionLocalProvider(
            LocalNavigatorSaver provides parcelableNavigatorSaver()
        ) {
            Navigator(HomeScreen())
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
