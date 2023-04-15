package dev.berwyn.jellybox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.ui.JellyboxApp
import dev.berwyn.jellybox.ui.util.LocalActivity
import dev.berwyn.jellybox.ui.util.detectNavigationType
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    val appState: ApplicationState by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CompositionLocalProvider(LocalActivity provides this) {
                appState.navigationType = detectNavigationType()

                JellyboxApp(
                    navigationType = appState.navigationType,
                    navigationHidden = appState.navigationHidden,
                )
            }
        }
    }
}
