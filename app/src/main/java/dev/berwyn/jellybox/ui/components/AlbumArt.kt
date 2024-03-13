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
import dev.berwyn.jellybox.ui.locals.LocalJellyfinClient
import org.jellyfin.sdk.api.client.extensions.imageApi
import org.jellyfin.sdk.model.api.ImageType

// TODO: Proxy through ServerAlbum to ensure the correct remote ID
@Composable
fun AlbumArt(
    album: Album,
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
        val client = LocalJellyfinClient.current
        val url = remember(album) {
            client.imageApi.getItemImageUrl(album.id, ImageType.PRIMARY)
        }

        CoilImage(
            imageModel = { url },
            modifier = modifier.then(aspectModifier)
        )
    }
}
