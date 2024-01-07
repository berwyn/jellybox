package dev.berwyn.jellybox.media

import android.util.Log
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class JellyboxMediaService : MediaSessionService() {

    companion object {
        const val TAG = "JellyboxMediaService"
    }

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Preparing ExoPlayer instance")

        val player = ExoPlayer.Builder(
            this,
            DefaultRenderersFactory(this@JellyboxMediaService).apply {
                setEnableDecoderFallback(true)
                setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
            },
            DefaultMediaSourceFactory(
                OkHttpDataSource.Factory(
                    OkHttpClient.Builder()
                        .addInterceptor(
                            HttpLoggingInterceptor().apply {
                                level = HttpLoggingInterceptor.Level.BASIC
                            }
                        )
                        .build()
                )
            ),
        )
            .build()
            .apply {
                addAnalyticsListener(EventLogger())
            }

        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onDestroy() {
        mediaSession?.run {
            Log.d(TAG, "Releasing ExoPlayer instance")

            player.release()
            release()

            mediaSession = null
        }

        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession
}
