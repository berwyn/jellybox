package dev.berwyn.jellybox.ui.screens.album

import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.berwyn.jellybox.data.local.Album
import dev.berwyn.jellybox.data.local.Jellybox
import dev.berwyn.jellybox.data.local.Track
import dev.berwyn.jellybox.data.store.AlbumStore
import dev.berwyn.jellybox.data.store.AlbumTrackStore
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.mobilenativefoundation.store.store5.StoreReadRequest
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
data class AlbumScreenModel(
    private val jellybox: Jellybox,
    private val albumStore: AlbumStore,
    private val trackStore: AlbumTrackStore
) : ScreenModel {

    sealed class State {
        data object Loading : State()

        data class Ready(
            val album: Album,
            val tracks: ImmutableList<Track>,
        ) : State()
    }

    private val _idFlow = MutableSharedFlow<UUID>(replay = 1)

    private val _albumFlow = _idFlow.flatMapLatest { id ->
        albumStore.stream(StoreReadRequest.cached(id, refresh = true))
            .map { it.dataOrNull() }
    }

    private val _tracksFlow = _albumFlow
        .filterNotNull()
        .flatMapLatest { album ->
            trackStore.stream(StoreReadRequest.cached(album, refresh = true))
                .map { response ->
                    response.dataOrNull()
                        .orEmpty()
                        .toImmutableList()
                }
        }

    val state: StateFlow<State> = screenModelScope.launchMolecule(RecompositionMode.Immediate) {
        AlbumScreenPresenter(
            albumFlow = _albumFlow,
            tracksFlow = _tracksFlow,
        )
    }

    fun updateId(id: UUID) {
        screenModelScope.launch {
            _idFlow.emit(id)
        }
    }
}


