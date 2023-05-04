package dev.berwyn.jellybox.ui.media

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import dev.berwyn.jellybox.ui.previews.DevicePreviews
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.theme.JellyboxTheme

@Composable
fun MediaCollection(
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
        item(span = { GridItemSpan(maxLineSpan) }) {
            MediaCollectionLatest()
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Divider()
        }

        items(35) {
            Card(Modifier.aspectRatio(2 / 3f, true)) {

            }
        }
    }
}

@Composable
@DevicePreviews
@ThemePreviews
private fun MediaCollectionPreview() {
    JellyboxTheme {
        MediaCollection()
    }
}
