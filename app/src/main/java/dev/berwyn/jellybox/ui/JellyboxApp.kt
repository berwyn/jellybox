package dev.berwyn.jellybox.ui

import androidx.compose.runtime.Composable
import cafe.adriel.lyricist.ProvideStrings
import dev.berwyn.jellybox.ui.theme.JellyboxTheme

@Composable
fun JellyboxApp() {
    ProvideStrings {
        JellyboxTheme {
            JellyboxNavigation()
        }
    }
}
