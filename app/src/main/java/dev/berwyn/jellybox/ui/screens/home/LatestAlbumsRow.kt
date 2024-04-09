package dev.berwyn.jellybox.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import cafe.adriel.lyricist.LocalStrings
import cafe.adriel.voyager.navigator.LocalNavigator
import dev.berwyn.jellybox.data.local.Album
import dev.berwyn.jellybox.data.local.Server
import dev.berwyn.jellybox.data.store.LatestAlbumStore
import dev.berwyn.jellybox.data.store.Stores
import dev.berwyn.jellybox.ui.components.AlbumArt
import dev.berwyn.jellybox.ui.previews.DynamicColourPreviews
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.screens.album.AlbumScreen
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.takeWhile
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import java.util.UUID

@Composable
fun LatestAlbumsRow(
    server: Server,
    modifier: Modifier = Modifier,
    latestAlbumStore: LatestAlbumStore = koinInject(named(Stores.LatestAlbums))
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var albums: ImmutableList<Album> by remember {
        mutableStateOf(persistentListOf())
    }

    var isLoading: Boolean by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(server) {
        latestAlbumStore.stream(StoreReadRequest.cached(server, refresh = true))
            .takeWhile { lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) }
            .collect {
                when (it) {
                    is StoreReadResponse.Loading -> {
                        isLoading = true
                    }
                    is StoreReadResponse.Data -> {
                        albums = it.dataOrNull().orEmpty().toImmutableList()
                        isLoading = false
                    }
                    is StoreReadResponse.NoNewData -> {}
                    is StoreReadResponse.Error -> {
                        Log.e("Latest Albums", it.errorMessageOrNull() ?: "Unknown error")
                        // TODO: Handle error
                    }
                }
            }
    }

    LatestAlbumsRow(albums = albums, modifier = modifier, isLoading = isLoading)
}

@Composable
fun LatestAlbumsRow(
    albums: ImmutableList<Album>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .then(modifier),
    ) {
        val strings = LocalStrings.current
        val navigator = LocalNavigator.current

        Text(
            text = strings.homeScreen.latestAlbums,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (isLoading) {
                items(3, key = { index -> "loading-${index}" }, contentType = { "loading" }) {
                    Box(modifier = Modifier.aspectRatio(1.0f).background(MaterialTheme.colorScheme.secondaryContainer))
                }
            } else {
                items(albums.count(), key = { index -> albums[index].id }, contentType = { "album" }) { index ->
                    val album = albums[index]

                    AlbumArt(
                        album = album,
                        modifier = Modifier
                            .width(192.dp)
                            .clickable {
                                navigator?.push(AlbumScreen(album.id))
                            },
                    )
                }
            }
        }
    }
}

@Composable
@ThemePreviews
@DynamicColourPreviews
private fun LatestAlbumRowPreview() {
    JellyboxTheme {
        LatestAlbumsRow(
            albums = persistentListOf(
                Album(id = UUID.randomUUID(), name = "Album 1", duration = 0L),
                Album(id = UUID.randomUUID(), name = "Album 2", duration = 0L),
                Album(id = UUID.randomUUID(), name = "Album 3", duration = 0L),
                Album(id = UUID.randomUUID(), name = "Album 4", duration = 0L),
                Album(id = UUID.randomUUID(), name = "Album 5", duration = 0L),
            ),
        )
    }
}
