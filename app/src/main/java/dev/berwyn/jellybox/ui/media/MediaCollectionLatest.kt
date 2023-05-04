package dev.berwyn.jellybox.ui.media

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.berwyn.jellybox.R
import dev.berwyn.jellybox.ui.previews.DynamicColourPreviews
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.theme.JellyboxTheme

@Composable
fun MediaCollectionLatest(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(stringResource(R.string.title_latest), style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(7) {
                item {
                    Card(modifier = Modifier
                        .width(192.dp)
                        .aspectRatio(2 / 3f, true)) {

                    }
                }
            }

        }
    }
}

@Composable
@ThemePreviews
@DynamicColourPreviews
private fun MediaCollectionLatestPreview() {
    JellyboxTheme {
        MediaCollectionLatest()
    }
}
