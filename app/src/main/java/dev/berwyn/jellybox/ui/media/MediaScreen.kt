package dev.berwyn.jellybox.ui.media

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import dev.berwyn.jellybox.ui.previews.PreviewReplacer
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.mobilenativefoundation.store.store5.Store
import java.util.UUID

@Suppress("unused")
private const val TAG = "Media Screen"

@Composable
fun MediaScreen(
    applicationState: ApplicationState,
    modifier: Modifier = Modifier,
    onItemSelected: (id: UUID) -> Unit = {},
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
            modifier = modifier,
        ) {
            val tabs = it.orEmpty().map { collection ->
                MediaTab(
                    title = collection.name,
                    collectionId = collection.id,
                )
            }.toImmutableList()

            MediaScreen(
                tabs = tabs,
                modifier = Modifier.fillMaxSize(),
                onItemSelected = onItemSelected,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun MediaScreen(
    tabs: ImmutableList<MediaTab>,
    modifier: Modifier = Modifier,
    onItemSelected: (id: UUID) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { tabs.size },
    )

    Column(modifier = modifier) {
        ScrollableTabRow(
            modifier = Modifier.fillMaxWidth().statusBarsPadding(),
            selectedTabIndex = pagerState.currentPage,
            divider = {},
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(tab.title) },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            key = { tabs[it].collectionId },
            modifier = Modifier.fillMaxSize()
        ) { index ->
            PreviewReplacer(title = "Collection") {
                val tab = remember(tabs, index) {
                    tabs[index]
                }

                MediaCollection(
                    tab.collectionId,
                    modifier = Modifier.fillMaxSize(),
                    onItemSelected = onItemSelected,
                )
            }
        }
    }
}

@Composable
@ThemePreviews
@DevicePreviews
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
