package dev.berwyn.jellybox.ui.media

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.ui.previews.DevicePreviews
import dev.berwyn.jellybox.ui.previews.DynamicColourPreviews
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun MediaScreen(
    applicationState: ApplicationState,
    modifier: Modifier = Modifier,
) {
    // TODO: Actually fetch collections and pass them down
    MediaScreen(
        modifier = modifier,
        tabs = persistentListOf(
            MediaTab("Movies", UUID.randomUUID().toString()),
            MediaTab("Music", UUID.randomUUID().toString()),
            MediaTab("Shows", UUID.randomUUID().toString()),
        )
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
private fun MediaScreen(
    tabs: ImmutableList<MediaTab>,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    Column(modifier = modifier) {
        TopAppBar(
            title = {
                Text("Media")
            },
        )

        ScrollableTabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = pagerState.currentPage,
            divider = {}
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(tab.title) }
                )
            }
        }

        HorizontalPager(
            pageCount = tabs.size,
            state = pagerState,
            key = { tabs[it].collectionId },
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        ) { index ->
            val tab = tabs[index]

            MediaCollection()
        }
    }
}

@Composable
@DevicePreviews
@ThemePreviews
@DynamicColourPreviews
private fun MediaScreenPreview() {
    JellyboxTheme {
        MediaScreen(
            tabs = persistentListOf(
                MediaTab("Movies", collectionId = UUID.randomUUID().toString()),
                MediaTab("Music", collectionId = UUID.randomUUID().toString()),
                MediaTab("Shows", collectionId = UUID.randomUUID().toString()),
            ),
        )
    }
}
