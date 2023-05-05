package dev.berwyn.jellybox.ui.media

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import dev.berwyn.jellybox.data.CollectionMediaItemStore
import dev.berwyn.jellybox.data.LatestMediaItemsStore
import dev.berwyn.jellybox.data.local.MediaCollectionWithItems
import dev.berwyn.jellybox.data.local.MediaItem
import dev.berwyn.jellybox.data.local.MediaItemType
import dev.berwyn.jellybox.ui.previews.DevicePreviews
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.koin.compose.koinInject
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import java.util.UUID

private const val TAG = "Media Collection"

@Composable
fun MediaCollection(
    collectionId: UUID,
    modifier: Modifier = Modifier,
    nestedScrollConnection: NestedScrollConnection? = null,
    itemsStore: Store<UUID, MediaCollectionWithItems> = koinInject(CollectionMediaItemStore),
    latestStore: Store<UUID, List<MediaItem>> = koinInject(LatestMediaItemsStore),
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var items: ImmutableList<MediaItem> by remember {
        mutableStateOf(persistentListOf())
    }

    var latestItems: ImmutableList<MediaItem> by remember {
        mutableStateOf(persistentListOf())
    }

    LaunchedEffect(collectionId) {
        itemsStore
            .stream(StoreReadRequest.cached(collectionId, refresh = true))
            .flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { response ->
                when (response) {
                    is StoreReadResponse.Data -> {
                        items = response.value.items.toImmutableList()
                    }

                    else -> {
                        // TODO: Handle other invariants
                        Log.d(TAG, "Other status in items")
                    }
                }

            }
    }

    LaunchedEffect(collectionId) {
        latestStore
            .stream(StoreReadRequest.cached(collectionId, refresh = true))
            .flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { response ->
                when (response) {
                    is StoreReadResponse.Data -> {
                        latestItems = response.value.toImmutableList()
                    }

                    else -> {
                        // TODO: Handle other invariants
                        Log.d(TAG, "Other status in latest items")
                    }
                }
            }
    }

    MediaCollection(
        items = items,
        latestItems = latestItems,
        modifier = modifier,
        nestedScrollConnection = nestedScrollConnection,
    )
}

@Composable
fun MediaCollection(
    items: ImmutableList<MediaItem>,
    latestItems: ImmutableList<MediaItem>,
    modifier: Modifier = Modifier,
    nestedScrollConnection: NestedScrollConnection? = null,
) {
    LazyVerticalGrid(
        modifier = if (nestedScrollConnection != null) modifier.nestedScroll(nestedScrollConnection) else modifier,
        columns = GridCells.Adaptive(minSize = 128.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (latestItems.size > 0) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                MediaCollectionLatest(latestItems)
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Divider()
            }
        }

        items(items.size, key = { index -> items[index].id }) {
            MediaItemCard(item = items[it])
        }
    }
}

@Composable
@DevicePreviews
@ThemePreviews
private fun MediaCollectionPreview() {
    val filler = mutableListOf<MediaItem>()
    repeat(35) {
        filler.add(
            MediaItem(
                id = UUID.randomUUID(),
                parentId = UUID.randomUUID(),
                name = "Example Item",
                imageAspectRatio = 2 / 3.0,
                type = MediaItemType.MOVIE,
            )
        )
    }

    val latestFiller = mutableListOf<MediaItem>()
    repeat(7) {
        latestFiller.add(
            MediaItem(
                id = UUID.randomUUID(),
                parentId = UUID.randomUUID(),
                name = "Example Item",
                imageAspectRatio = 2 / 3.0,
                type = MediaItemType.MOVIE,
            )
        )
    }

    JellyboxTheme {
        MediaCollection(
            items = filler.toImmutableList(),
            latestItems = latestFiller.toImmutableList(),
        )
    }
}
