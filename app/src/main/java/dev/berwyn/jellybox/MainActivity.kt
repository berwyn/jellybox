package dev.berwyn.jellybox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.ui.JellyboxNavigation
import dev.berwyn.jellybox.ui.previews.ThemePreview
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import org.jellyfin.sdk.Jellyfin
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var jellyfin: Jellyfin

    @Inject
    lateinit var appState: ApplicationState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JellyboxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    JellyboxNavigation { nav ->
                        if (appState.jellyfinClient == null) {
                            nav.navigate("onboarding") {
                                popUpTo(0)
                            }
                        }
                    }
                }
            }
        }
    }
}
