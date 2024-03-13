package dev.berwyn.jellybox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.skydoves.landscapist.coil.CoilImage
import dev.berwyn.jellybox.data.local.Album
import dev.berwyn.jellybox.ui.locals.JellyfinClientState
import dev.berwyn.jellybox.ui.locals.LocalJellyfinClientState
import org.jellyfin.sdk.api.client.extensions.imageApi
import org.jellyfin.sdk.model.api.ImageType
import java.util.UUID

@Composable
fun AlbumArt(
    album: Album,
    modifier: Modifier = Modifier,
) {
    AlbumArt(albumId = album.id, modifier = modifier)
}

@Composable
fun AlbumArt(
    albumId: UUID,
    modifier: Modifier = Modifier,
) {
    val aspectModifier = Modifier.aspectRatio(1.0f)

    if (LocalInspectionMode.current) {
        Box(
            modifier = modifier
                .then(aspectModifier)
                .then(Modifier.background(MaterialTheme.colorScheme.secondaryContainer))
        )
    } else {
        when (val state = LocalJellyfinClientState.current) {
            is JellyfinClientState.Unset -> {}
            is JellyfinClientState.Configured -> {
                val (client) = state

                val url = remember(albumId) {
                    client.imageApi.getItemImageUrl(albumId, ImageType.PRIMARY)
                }

                CoilImage(
                    imageModel = { url },
                    modifier = modifier.then(aspectModifier)
                )
            }
        }
    }
}
