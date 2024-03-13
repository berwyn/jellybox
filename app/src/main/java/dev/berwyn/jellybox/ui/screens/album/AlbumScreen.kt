package dev.berwyn.jellybox.ui.screens.album

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import dev.berwyn.jellybox.data.local.Album
import dev.berwyn.jellybox.ui.components.AlbumArt
import dev.berwyn.jellybox.ui.screens.ParcelableScreen
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
class AlbumScreen(val albumId: UUID) : ParcelableScreen {
    @Composable
    override fun Content() {
        val model = getScreenModel<AlbumScreenModel>()
        val state by model.state.collectAsState()

        LaunchedEffect(albumId) {
            model.updateId(albumId)
        }

        when (state) {
            is AlbumScreenModel.State.Loading -> {
                Box {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            is AlbumScreenModel.State.Ready -> {
                val (album, tracks) = state as AlbumScreenModel.State.Ready

                Scaffold { contentPadding ->
                    LazyColumn(contentPadding = contentPadding) {
                        item(contentType = "header") {
                            Header(album)
                        }

                        items(tracks.size, key = { index -> tracks[index].id }, contentType = { "track" }) { index ->
                            val track = tracks[index]

                            Row {
                                Text(text = track.name)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Header(album: Album) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AlbumArt(album = album, modifier = Modifier.width(192.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = album.name,
                    style = MaterialTheme.typography.headlineSmall
                )

                // TODO: Release year + runtime
            }
        }
    }
}
