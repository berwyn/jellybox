package dev.berwyn.jellybox.ui.media

import android.content.ComponentName
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.data.MediaItemStore
import dev.berwyn.jellybox.data.local.MediaItem
import dev.berwyn.jellybox.data.local.MediaItemType
import dev.berwyn.jellybox.media.JellyboxMediaService
import dev.berwyn.jellybox.ui.locals.LocalActivity
import dev.berwyn.jellybox.ui.locals.LocalApplicationState
import org.jellyfin.sdk.api.client.extensions.imageApi
import org.jellyfin.sdk.api.client.extensions.videosApi
import org.jellyfin.sdk.model.api.ImageType
import org.koin.compose.koinInject
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import java.util.UUID
import androidx.media3.common.MediaItem as ExoMediaItem

@Composable
fun MediaPlayer(
    itemId: String,
    modifier: Modifier = Modifier,
    store: Store<UUID, MediaItem> = koinInject(MediaItemStore),
) {
    var item: MediaItem? by remember { mutableStateOf(null) }

    LaunchedEffect(itemId) {
        store.stream(StoreReadRequest.cached(UUID.fromString(itemId), refresh = false))
            .collect { response ->
                when (response) {
                    is StoreReadResponse.Data -> {
                        item = response.value
                    }

                    else -> {
                        // TODO
                    }
                }
            }
    }

    if (item == null) {
        Box(modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        MediaPlayer(item = item!!, modifier = modifier)
    }
}

@Composable
fun MediaPlayer(
    item: MediaItem,
    modifier: Modifier = Modifier,
    applicationState: ApplicationState = koinInject()
) {
    var mediaItem: ExoMediaItem? by remember { mutableStateOf(null) }

    LaunchedEffect(item) {
        applicationState.ensureSession().onSuccess { client ->
            val uri: String = when (item.type) {
                MediaItemType.MOVIE -> client.videosApi.getVideoStreamByContainerUrl(
                    itemId = item.id,
                    container = "mp4",
                    videoCodec = "h264",
                    audioCodec = "aac",
                )

                else -> TODO("Not implemented")
            }

            val artworkUri = client.imageApi.getItemImageUrl(
                itemId = item.id,
                imageType = ImageType.PRIMARY,
                maxWidth = 512,
                maxHeight = 512,
            )

            mediaItem = ExoMediaItem.Builder()
                .setUri(uri)
                .setMediaId(item.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(item.name)
                        .setArtworkUri(Uri.parse(artworkUri))
                        .build()
                )
                .build()
        }
    }

    if (mediaItem == null) {
        Box(modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        MediaPlayer(item = mediaItem!!, modifier = modifier)
    }
}

@Composable
fun MediaPlayer(
    item: ExoMediaItem,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current
    val appState = LocalApplicationState.current

    val sessionToken: SessionToken = remember {
        SessionToken(
            activity,
            ComponentName(activity, JellyboxMediaService::class.java)
        )
    }

    var controllerFuture: ListenableFuture<MediaController>? by remember { mutableStateOf(null) }
    var controller: MediaController? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        appState.hideNavigation()

        onDispose {
            appState.showNavigation()
        }
    }

    DisposableEffect(sessionToken) {
        controllerFuture = MediaController.Builder(activity, sessionToken)
            .buildAsync()
            .apply {
                addListener(
                    {
                        Log.d("MediaPlayer", "Got MediaController instance")
                        controller = get().apply {
                            setMediaItem(item)
                            prepare()
                            play()
                        }
                    },
                    MoreExecutors.directExecutor(),
                )
            }

        onDispose {
            controllerFuture?.let(MediaController::releaseFuture)
        }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }
        },
        modifier = modifier,
        update = { view ->
            view.player = controller
        }
    )
}
