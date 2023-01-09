package dev.berwyn.jellybox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.ui.JellyboxApp
import dev.berwyn.jellybox.ui.util.LocalWidthSizeClass
import org.jellyfin.sdk.Jellyfin
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var jellyfin: Jellyfin

    @Inject
    lateinit var appState: ApplicationState

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val sizeClass = calculateWindowSizeClass(activity = this)

            CompositionLocalProvider(LocalWidthSizeClass provides sizeClass.widthSizeClass) {
                JellyboxApp(hasServersConfigured = appState.hasServersConfigured)
            }
        }
    }
}
