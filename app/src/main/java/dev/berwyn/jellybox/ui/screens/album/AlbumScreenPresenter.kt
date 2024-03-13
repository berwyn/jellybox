package dev.berwyn.jellybox.ui.screens.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.berwyn.jellybox.data.local.Album
import dev.berwyn.jellybox.data.local.Track
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow

@Composable
fun AlbumScreenPresenter(
    albumFlow: Flow<Album?>,
    tracksFlow: Flow<ImmutableList<Track>>,
): AlbumScreenModel.State {
    val album: Album? by albumFlow.collectAsState(initial = null)
    val tracks by tracksFlow.collectAsState(initial = persistentListOf())

    return when {
        album == null -> AlbumScreenModel.State.Loading
        else -> AlbumScreenModel.State.Ready(album!!, tracks)
    }
}
