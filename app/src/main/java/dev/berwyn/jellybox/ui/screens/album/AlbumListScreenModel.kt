package dev.berwyn.jellybox.ui.screens.album

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.berwyn.jellybox.data.JellyfinServerRepository
import dev.berwyn.jellybox.data.local.Album
import dev.berwyn.jellybox.data.local.Server
import dev.berwyn.jellybox.data.store.AlbumsStore
import dev.berwyn.jellybox.data.store.Stores
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

data class AlbumListScreenModel(
    val serverRepository: JellyfinServerRepository,
    val albumsStore: AlbumsStore,
) : ScreenModel {

    sealed class State {
        data object Loading : State()
        data class Ready(val albums: ImmutableList<Album>) : State()
        data class Error(val error: String?) : State()
    }

    private val _serverFlow = serverRepository.getSelectedServer()

    val state: StateFlow<State> = screenModelScope.launchMolecule(RecompositionMode.Immediate) {
        AlbumListScreenPresenter(
            serverFlow = _serverFlow,
        )
    }
}

@Composable
@OptIn(ExperimentalCoroutinesApi::class)
private fun AlbumListScreenPresenter(serverFlow: Flow<Server?>): AlbumListScreenModel.State {
    val store = koinInject<AlbumsStore>(named(Stores.Albums))

    val state by remember {
        serverFlow
            .flatMapLatest {
                when (it) {
                    null -> flowOf()
                    else -> store.stream(StoreReadRequest.cached(it, refresh = true))
                }
            }
            .onEach { Log.d("AlbumListScreenPresenter", it.toString()) }
            .map { state ->
                when (state) {
                    is StoreReadResponse.Loading -> AlbumListScreenModel.State.Loading
                    is StoreReadResponse.Data -> AlbumListScreenModel.State.Ready(state.value)
                    is StoreReadResponse.Error -> AlbumListScreenModel.State.Error(state.errorMessageOrNull())
                    else -> {
                        AlbumListScreenModel.State.Loading
                    }
                }
            }
    }.collectAsState(initial = AlbumListScreenModel.State.Loading)

    return state;
}
