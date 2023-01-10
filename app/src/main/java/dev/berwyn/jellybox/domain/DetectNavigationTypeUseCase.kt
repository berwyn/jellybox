package dev.berwyn.jellybox.domain

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import dev.berwyn.jellybox.ui.navigation.NavigationType
import dev.berwyn.jellybox.ui.util.LocalActivity
import javax.inject.Inject

class DetectNavigationTypeUseCase @Inject constructor() {
    @Composable
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    operator fun invoke(): NavigationType {
        val sizeClass = calculateWindowSizeClass(activity = LocalActivity.current)

        // TODO: should height class factor into this?
        return when {
            sizeClass.widthSizeClass != WindowWidthSizeClass.Compact -> NavigationType.Rail
            else -> NavigationType.Bar
        }
    }
}
