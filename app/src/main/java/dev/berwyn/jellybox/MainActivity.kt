package dev.berwyn.jellybox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.skydoves.landscapist.coil.LocalCoilImageLoader
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.ui.JellyboxApp
import dev.berwyn.jellybox.ui.locals.LocalApplicationState
import dev.berwyn.jellybox.ui.locals.LocalActivity
import dev.berwyn.jellybox.ui.util.detectNavigationType
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    val appState: ApplicationState by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        val imageLoader = ImageLoader.Builder(this)
            .allowHardware(true)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .maxSizePercent(0.25)
                    .directory(cacheDir.resolve("image_cache"))
                    .build()
            }
            .build()

        setContent {
            CompositionLocalProvider(
                LocalActivity provides this,
                LocalApplicationState provides appState,
                LocalCoilImageLoader provides imageLoader,
            ) {
                appState.navigationType = detectNavigationType()

                JellyboxApp(
                    navigationType = appState.navigationType,
                    navigationHidden = appState.navigationHidden,
                )
            }
        }
    }
}
