package dev.berwyn.jellybox.ui.util

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import dev.berwyn.jellybox.ui.locals.LocalWindowSizeClass
import dev.berwyn.jellybox.ui.navigation.NavigationType

@Composable
fun detectNavigationType(): NavigationType {
    val sizeClass = LocalWindowSizeClass.current

    return when {
        sizeClass.widthSizeClass > WindowWidthSizeClass.Compact -> NavigationType.Rail
        else -> NavigationType.Bar
    }
}
