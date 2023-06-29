package dev.berwyn.jellybox.ui.media

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.data.local.MediaItem
import dev.berwyn.jellybox.data.local.MediaItemType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import org.jellyfin.sdk.api.client.extensions.imageApi
import org.jellyfin.sdk.model.api.ImageType
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import java.util.UUID

class MediaItemScreenViewModel(
    val savedStateHandle: SavedStateHandle,
    val applicationState: ApplicationState,
    val itemStore: Store<UUID, MediaItem>,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val item: Flow<MediaItem?> = savedStateHandle
        .getStateFlow<String?>("id", initialValue = null)
        .filterNotNull()
        .map(UUID::fromString)
        .flatMapConcat {
            itemStore.stream(StoreReadRequest.cached(it, refresh = false))
        }
        .map { response ->
            when (response) {
                is StoreReadResponse.Data -> { response.value }
                else -> { null }
            }
        }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    val backdropUrl: Flow<String?> = item
        .map {
            it?.let { item ->
                val imageType = when (item.type) {
                    MediaItemType.MUSIC_ALBUM -> ImageType.PRIMARY
                    else -> ImageType.PRIMARY
                }

                applicationState.jellyfinClient?.imageApi?.getItemImageUrl(
                    itemId = item.id,
                    imageType = imageType,
                    maxWidth = 512,
                    maxHeight = 512,
                )
            }
        }
}
