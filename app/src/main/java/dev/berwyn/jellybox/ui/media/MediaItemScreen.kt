package dev.berwyn.jellybox.ui.media

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.berwyn.jellybox.data.local.JellyboxDatabase
import dev.berwyn.jellybox.data.local.MediaItem
import org.koin.compose.koinInject
import java.util.UUID

@Composable
fun MediaItemScreen(
    itemId: UUID,
    database: JellyboxDatabase = koinInject(),
) {
    var item: MediaItem? by remember { mutableStateOf(null) }

    LaunchedEffect(itemId) {
        item = database.mediaItemDao()
            .findById(itemId)
    }

    Column {
        if (item == null) {
            CircularProgressIndicator()
        }

        item?.let {
            Text(it.name)
        }
    }
}
