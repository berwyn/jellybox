package dev.berwyn.jellybox.ui.media

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import dev.berwyn.jellybox.data.local.MediaItem
import dev.berwyn.jellybox.ui.locals.LocalApplicationState
import dev.berwyn.jellybox.ui.previews.PreviewReplacer
import org.jellyfin.sdk.api.client.extensions.imageApi
import org.jellyfin.sdk.model.api.ImageFormat
import org.jellyfin.sdk.model.api.ImageType

@Composable
fun MediaItemCard(item: MediaItem, modifier: Modifier = Modifier) {
    Card(modifier = modifier.aspectRatio(item.imageAspectRatio.toFloat(), true)) {
        Box(modifier = Modifier.fillMaxSize()) {
            PreviewReplacer(title = "Poster", modifier = Modifier.fillMaxSize()) {

                val appState = LocalApplicationState.current
                var imageUrl: String? by remember { mutableStateOf(null) }

                LaunchedEffect(item) {
                    appState.jellyfinClient?.let {
                        imageUrl = it.imageApi.getItemImageUrl(
                            item.id,
                            imageType = ImageType.PRIMARY,
                            format = ImageFormat.WEBP,
                        )
                    }
                }

                CoilImage(
                    imageModel = { imageUrl },
                    modifier = Modifier.fillMaxSize(),
                    component = rememberImageComponent {
                        add(
                            ShimmerPlugin(
                                baseColor = MaterialTheme.colorScheme.surfaceVariant,
                                highlightColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )
                    }
                )
            }
        }
    }
}
