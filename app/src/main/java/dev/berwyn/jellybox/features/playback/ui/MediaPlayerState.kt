package dev.berwyn.jellybox.features.playback.ui

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.data.local.Track
import dev.berwyn.jellybox.features.playback.JellyboxMediaService
import kotlinx.coroutines.launch
import org.jellyfin.sdk.api.client.extensions.audioApi
import org.jellyfin.sdk.api.client.extensions.imageApi
import org.jellyfin.sdk.model.api.ImageType
import androidx.media3.common.MediaItem as ExoMediaItem
import org.jellyfin.sdk.api.client.ApiClient as JellyfinApiClient

class MediaPlayerState(
    context: Context,
    private val applicationState: ApplicationState,
) : ViewModel() {
    companion object {
        private const val TAG = "MediaPlayerState"
    }

    var mediaController: MediaController? = null
        private set

    private var _mediaQueue: MutableList<Track> = mutableListOf()
    val mediaQueue: List<Track>
        get() = _mediaQueue

    var isLoading: Boolean = true
        private set

    private var sessionToken: SessionToken = SessionToken(
        context,
        ComponentName(context, JellyboxMediaService::class.java)
    )

    private var controllerFuture: ListenableFuture<MediaController> = MediaController.Builder(context, sessionToken)
        .buildAsync()
        .apply {
            addListener(
                {
                    Log.d(TAG, "Acquired media controller")
                    mediaController = get().apply {
                        playWhenReady = true
                        prepare()
                    }
                },
                MoreExecutors.directExecutor()
            )
        }

    private lateinit var jellyfinClient: JellyfinApiClient

    init {
        addCloseable {
            MediaController.releaseFuture(controllerFuture)
        }

        applicationState.currentClient?.let {
            jellyfinClient = it
        }
    }

    fun playItem(item: Track) {
        _mediaQueue.clear()
        _mediaQueue.add(item)

        val uri = jellyfinClient.audioApi.getAudioStreamUrl(item.id)
        val artworkUri = jellyfinClient.imageApi.getItemImageUrl(item.id, imageType = ImageType.PRIMARY, maxWidth = 512, maxHeight = 512)

        val exoMediaItem = ExoMediaItem.Builder()
            .setUri(uri)
            .setMediaId(item.id.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(item.name)
                    .setArtworkUri(Uri.parse(artworkUri))
                    .build()
            )
            .build()

        mediaController?.addMediaItem(exoMediaItem)
    }
}
