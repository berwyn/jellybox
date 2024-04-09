package dev.berwyn.jellybox.ui

import androidx.compose.animation.Crossfade
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import dev.berwyn.jellybox.ui.previews.DevicePreviews
import dev.berwyn.jellybox.ui.previews.DynamicColourPreviews
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.screens.album.AlbumsTab
import dev.berwyn.jellybox.ui.screens.home.HomeTab
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

val tabs: ImmutableList<Tab> = persistentListOf(
    HomeTab,
    AlbumsTab
)

@Composable
@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
fun JellyboxNavigation() {
    BottomSheetNavigator { sheetNav ->
        TabNavigator(HomeTab) { tabNav ->
            NavigationSuiteScaffold(
                navigationSuiteItems = {
                    tabs.forEach {
                        item(
                            selected = tabNav.current == it,
                            icon = { Icon(it.options.icon!!, contentDescription = null) },
                            label = { Text(it.options.title) },
                            onClick = { tabNav.current = it }
                        )
                    }
                }
            ) {
                Crossfade(targetState = tabNav.current, label = "Tab") { tab ->
                    tab.Content()
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
