package dev.berwyn.jellybox.ui.previews

import androidx.compose.runtime.Composable
import cafe.adriel.lyricist.ProvideStrings
import dev.berwyn.jellybox.ui.theme.JellyboxTheme

@Composable
fun PreviewHost(content: @Composable () -> Unit) = JellyboxTheme {
    ProvideStrings {
        content()
    }
}
