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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.data.MediaCollectionStore
import dev.berwyn.jellybox.data.local.MediaCollection
import dev.berwyn.jellybox.ui.data.DataLoader
import dev.berwyn.jellybox.ui.previews.DevicePreviews
import dev.berwyn.jellybox.ui.previews.DynamicColourPreviews
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.mobilenativefoundation.store.store5.Store
import java.util.UUID

private const val TAG = "Media Screen"

@Composable
fun MediaScreen(
    applicationState: ApplicationState,
    modifier: Modifier = Modifier,
    store: Store<UUID, List<MediaCollection>> = koinInject(MediaCollectionStore)
) {
    var sessionVerified by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        applicationState.ensureSession().fold(
            onSuccess = { sessionVerified = true },
            // TODO: Actual error handling
            onFailure = { error("It's all fucked") }
        )
    }

    if (sessionVerified) {
        DataLoader(
            store = store,
            id = applicationState.jellyfinClient!!.userId!!,
            modifier = modifier
        ) {
            val tabs = it.orEmpty().map {
                MediaTab(
                    title = it.name,
                    collectionId = it.id,
                )
            }.toImmutableList()

            MediaScreen(tabs = tabs, modifier = Modifier.fillMaxSize())
        }
    }
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

            MediaCollection(tab.collectionId, modifier = Modifier.fillMaxSize())
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
                MediaTab("Movies", collectionId = UUID.randomUUID()),
                MediaTab("Music", collectionId = UUID.randomUUID()),
                MediaTab("Shows", collectionId = UUID.randomUUID()),
            ),
        )
    }
}
