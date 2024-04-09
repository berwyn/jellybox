package dev.berwyn.jellybox.ui.screens.album

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import dev.berwyn.jellybox.data.local.Album
import dev.berwyn.jellybox.data.local.Track
import dev.berwyn.jellybox.ui.components.AlbumArt
import dev.berwyn.jellybox.ui.screens.ParcelableScreen
import kotlinx.parcelize.Parcelize
import java.util.UUID
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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
                Scaffold { contentPadding ->
                    Box(Modifier.padding(contentPadding)) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            is AlbumScreenModel.State.Ready -> {
                val (album, tracks) = state as AlbumScreenModel.State.Ready

                Scaffold { contentPadding ->
                    LazyColumn(
                        contentPadding = contentPadding,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item(key = "header", contentType = "header") {
                            Header(album)
                        }

                        items(tracks.size, key = { index -> tracks[index].id }, contentType = { "track" }) { index ->
                            Track(track = tracks[index])
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Header(album: Album) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AlbumArt(album = album, modifier = Modifier.width(128.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = album.name,
                    style = MaterialTheme.typography.headlineSmall
                )

                val duration = remember(album) {
                    album.duration.toDuration(DurationUnit.MILLISECONDS).toComponents { hours, minutes, seconds, _ ->
                        val paddedMinutes = "$minutes".padStart(2, '0')
                        val paddedSeconds = "$seconds".padStart(2, '0')

                        if (hours > 0) {
                            "$hours:$paddedMinutes:$paddedSeconds"
                        } else {
                            "$paddedMinutes:$paddedSeconds"
                        }
                    }
                }

                Text(duration) // TODO: Release year
            }
        }
    }

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    private fun Track(track: Track) {
        val duration = remember(track) {
            track.duration.toDuration(DurationUnit.MILLISECONDS).toComponents { minutes, seconds, _ ->
                val paddedSeconds = "$seconds".padStart(2, '0')

                "$minutes:${paddedSeconds}"
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                track.name,
                softWrap = false,
                maxLines = 1,
                modifier = Modifier.weight(1f).basicMarquee(),
            )

            Spacer(Modifier.width(8.dp))

            Text(duration)
        }
    }
}
