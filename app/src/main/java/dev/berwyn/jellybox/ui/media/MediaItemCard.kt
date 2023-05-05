package dev.berwyn.jellybox.ui.media

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.skydoves.landscapist.coil.CoilImage
import dev.berwyn.jellybox.data.local.MediaItem
import dev.berwyn.jellybox.ui.locals.LocalApplicationState
import org.jellyfin.sdk.api.client.extensions.imageApi
import org.jellyfin.sdk.model.api.ImageFormat
import org.jellyfin.sdk.model.api.ImageType

@Composable
fun MediaItemCard(item: MediaItem, modifier: Modifier = Modifier) {

    var imageUrl: String? by remember { mutableStateOf(null) }

    if (!LocalInspectionMode.current) {
        val appState = LocalApplicationState.current

        LaunchedEffect(item) {
            appState.jellyfinClient?.let {
                imageUrl = it.imageApi.getItemImageUrl(
                    item.id,
                    imageType = ImageType.PRIMARY,
                    format = ImageFormat.WEBP,
                )
            }
        }
    }

    Card(modifier = modifier.aspectRatio(item.imageAspectRatio.toFloat(), true)) {
        Box(modifier = Modifier.fillMaxSize()) {
            CoilImage(
                imageModel = { imageUrl },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
