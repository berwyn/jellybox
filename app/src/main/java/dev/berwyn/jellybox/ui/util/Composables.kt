package dev.berwyn.jellybox.ui.util

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import dev.berwyn.jellybox.ui.navigation.NavigationType

@Composable
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun detectNavigationType(): NavigationType {
    val sizeClass = calculateWindowSizeClass(activity = LocalActivity.current)

    // TODO: should height class factor into this?
    return when {
        sizeClass.widthSizeClass != WindowWidthSizeClass.Compact -> NavigationType.Rail
        else -> NavigationType.Bar
    }
}
