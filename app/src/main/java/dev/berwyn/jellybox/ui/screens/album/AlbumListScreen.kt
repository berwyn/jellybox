package dev.berwyn.jellybox.ui.screens.album

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import dev.berwyn.jellybox.ui.components.AlbumArt
import dev.berwyn.jellybox.ui.screens.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class AlbumListScreen : ParcelableScreen {
    @Composable
    override fun Content() {
        val model = getScreenModel<AlbumListScreenModel>()
        val state by model.state.collectAsState()

        Crossfade(targetState = state, label = "Album List") {
            when (it) {
                is AlbumListScreenModel.State.Loading -> {
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }

                is AlbumListScreenModel.State.Ready -> {
                    val (albums) = it
                    val navigator = LocalNavigator.current

                    Scaffold { contentPadding ->
                        LazyVerticalGrid(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentPadding = contentPadding,
                            columns = GridCells.Adaptive(128.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(albums.size, key = { idx -> albums[idx].id }) { index ->
                                val album = albums[index]

                                AlbumArt(
                                    album = album,
                                    modifier = Modifier.clickable {
                                        navigator?.push(AlbumScreen(album.id))
                                    }
                                )
                            }
                        }
                    }
                }

                is AlbumListScreenModel.State.Error -> {
                    Scaffold { contentPadding ->
                        Column(Modifier.padding(contentPadding), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Error")

                            it.error?.let { error ->
                                Text(error)
                            }
                        }
                    }
                }
            }
        }
    }
}
