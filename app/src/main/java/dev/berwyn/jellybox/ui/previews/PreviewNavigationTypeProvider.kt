package dev.berwyn.jellybox.ui.previews

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.berwyn.jellybox.ui.navigation.NavigationType

class PreviewNavigationTypeProvider : PreviewParameterProvider<NavigationType> {
    override val values = sequenceOf(NavigationType.None, NavigationType.Bar, NavigationType.Rail)
}
