package dev.berwyn.jellybox.ui.media

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.berwyn.jellybox.R
import dev.berwyn.jellybox.data.local.MediaItem
import dev.berwyn.jellybox.data.local.MediaItemType
import dev.berwyn.jellybox.ui.previews.DynamicColourPreviews
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.util.UUID

@Composable
fun MediaCollectionLatest(
    items: ImmutableList<MediaItem>,
    modifier: Modifier = Modifier,
    onItemSelected: (id: UUID) -> Unit = {},
) {
    Column(modifier = modifier) {
        Text(stringResource(R.string.title_latest), style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items.size, key = { index -> items[index].id }) {
                val item = items[it]

                MediaItemCard(
                    item = item,
                    modifier = Modifier
                        .width(192.dp)
                        .clickable {
                            onItemSelected(item.id)
                        },
                )
            }
        }
    }
}

@Composable
@ThemePreviews
@DynamicColourPreviews
private fun MediaCollectionLatestPreview() {
    val filler = mutableListOf<MediaItem>()
    repeat(7) {
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
    JellyboxTheme {
        MediaCollectionLatest(filler.toImmutableList())
    }
}
