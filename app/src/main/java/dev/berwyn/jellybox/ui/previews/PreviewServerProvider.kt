package dev.berwyn.jellybox.ui.previews

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.berwyn.jellybox.data.local.JellyfinServer

class PreviewServerProvider : PreviewParameterProvider<JellyfinServer?> {
    override val values: Sequence<JellyfinServer?> = sequenceOf(
        null,
        JellyfinServer(
            id = 0,
            name = "Example Server",
            uri = "https://fake.url",
        )
    )
}
