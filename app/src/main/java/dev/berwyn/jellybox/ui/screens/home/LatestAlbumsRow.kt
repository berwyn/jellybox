package dev.berwyn.jellybox.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import dev.berwyn.jellybox.data.local.Album
import dev.berwyn.jellybox.ui.components.AlbumArt
import dev.berwyn.jellybox.ui.locals.JellyfinClientState
import dev.berwyn.jellybox.ui.locals.LocalJellyfinClientState
import dev.berwyn.jellybox.ui.previews.DynamicColourPreviews
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jellyfin.sdk.api.client.extensions.userLibraryApi
import org.jellyfin.sdk.model.api.BaseItemKind
import java.util.UUID

@Composable
fun LatestAlbumsRow(
    modifier: Modifier = Modifier,
) {
    val state = LocalJellyfinClientState.current
    var albums: ImmutableList<Album> by remember {
        mutableStateOf(persistentListOf())
    }

    LaunchedEffect(state) {
        if (state is JellyfinClientState.Configured) {
            val dtos by state.client.userLibraryApi
                .getLatestMedia(includeItemTypes = listOf(BaseItemKind.MUSIC_ALBUM), limit = 5)

            albums = dtos.map {
                Album(
                    id = it.id,
                    name = it.name!!,
                    duration = it.runTimeTicks!!.div(600_000_000),
                )
            }.toImmutableList()
        }
    }

    LatestAlbumsRow(albums = albums, modifier = modifier)
}

@Composable
fun LatestAlbumsRow(
    albums: ImmutableList<Album>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .then(modifier)) {
        // TODO: Translate + linkify
        Text(
            text = "Latest Albums",
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(albums.count(), key = { index -> albums[index].id }) { index ->
                val album = albums[index]

                AlbumArt(album = album, modifier = Modifier.width(192.dp))
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
