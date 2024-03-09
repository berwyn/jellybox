package dev.berwyn.jellybox.ui.previews

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.berwyn.jellybox.data.local.Server
import io.ktor.http.Url
import java.util.UUID

class PreviewServerProvider : PreviewParameterProvider<Server?> {
    override val values: Sequence<Server?> = sequenceOf(
        null,
        Server(
            id = UUID.randomUUID(),
            name = "Example Server",
            url = Url("https://fake.url"),
            isDefault = false,
            isSelected = false,
        )
    )
}
