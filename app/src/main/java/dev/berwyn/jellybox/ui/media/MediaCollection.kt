package dev.berwyn.jellybox.ui.media

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import dev.berwyn.jellybox.data.CollectionMediaItemStore
import dev.berwyn.jellybox.data.local.MediaCollectionWithItems
import dev.berwyn.jellybox.data.local.MediaItem
import dev.berwyn.jellybox.data.local.MediaItemType
import dev.berwyn.jellybox.ui.locals.LocalWindowSizeClass
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
    onItemSelected: (id: UUID) -> Unit = {},
    itemsStore: Store<UUID, MediaCollectionWithItems> = koinInject(CollectionMediaItemStore),
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var items: ImmutableList<MediaItem> by remember {
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

    MediaCollection(
        items = items,
        modifier = modifier,
        onItemSelected = onItemSelected,
    )
}

@Composable
private fun MediaCollection(
    items: ImmutableList<MediaItem>,
    modifier: Modifier = Modifier,
    onItemSelected: (id: UUID) -> Unit = {},
) {
    val sizeClass = LocalWindowSizeClass.current

    val cellSize = remember(sizeClass) {
        when (sizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 128.dp
            else -> 256.dp
        }
    }

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(minSize = cellSize),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            count = items.size,
            key = { index -> items[index].id },
            contentType = { "poster" }
        ) { index ->
            val item = items[index]

            MediaItemCard(
                item = item,
                modifier = Modifier.clickable {
                    onItemSelected(item.id)
                }
            )
        }
    }
}

@Composable
@DevicePreviews
@ThemePreviews
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
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

    // TODO: How in the hell are you supposed to actually calculate this in previews?
    CompositionLocalProvider(
        LocalWindowSizeClass provides WindowSizeClass.calculateFromSize(DpSize.Zero)
    ) {
        JellyboxTheme {
            MediaCollection(
                items = filler.toImmutableList(),
            )
        }
    }
}
