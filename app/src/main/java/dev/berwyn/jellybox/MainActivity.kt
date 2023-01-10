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
import dev.berwyn.jellybox.domain.DetectNavigationTypeUseCase
import dev.berwyn.jellybox.domain.SelectActiveServerUseCase
import dev.berwyn.jellybox.ui.JellyboxApp
import dev.berwyn.jellybox.ui.util.LocalActivity
import org.jellyfin.sdk.Jellyfin
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var jellyfin: Jellyfin

    @Inject
    lateinit var appState: ApplicationState

    @Inject
    lateinit var selectActiveServer: SelectActiveServerUseCase

    @Inject
    lateinit var detectNavigationType: DetectNavigationTypeUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CompositionLocalProvider(LocalActivity provides this) {
                appState.navigationType = detectNavigationType()

                JellyboxApp(
                    hasServersConfigured = appState.hasServersConfigured,
                    selectActiveServer = selectActiveServer,
                    navigationType = appState.navigationType,
                    savedServers = appState.savedServers,
                )
            }
        }
    }
}
