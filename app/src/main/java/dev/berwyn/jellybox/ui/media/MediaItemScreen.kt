package dev.berwyn.jellybox.ui.media

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.palette.graphics.Palette
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.palette.PalettePlugin
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import dev.berwyn.jellybox.ui.isCollapsed
import org.koin.androidx.compose.koinViewModel
import java.time.LocalTime
import java.util.UUID
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MediaItemScreen(
    modifier: Modifier = Modifier,
    viewModel: MediaItemScreenViewModel = koinViewModel(),
    onBackPressed: () -> Unit = {},
    onPlayPressed: (itemId: UUID) -> Unit = {}
) {
    val item by viewModel.item
        .collectAsStateWithLifecycle(initialValue = null)

    val backdropUrl by viewModel.backdropUrl
        .collectAsStateWithLifecycle(initialValue = null)

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var backdropPalette: Palette? by remember { mutableStateOf(null) }

    Box(modifier = modifier) {
        Column(Modifier.fillMaxSize()) {
            Box {
                CoilImage(
                    imageModel = { backdropUrl },
                    component = rememberImageComponent {
                        add(
                            ShimmerPlugin(
                                baseColor = MaterialTheme.colorScheme.surfaceVariant,
                                highlightColor = MaterialTheme.colorScheme.outlineVariant,
                            )
                        )

                        add(
                            PalettePlugin { backdropPalette = it }
                        )
                    },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.FillWidth,
                    ),
                    modifier = Modifier.matchParentSize(),
                )

                val scrimColour = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)

                LargeTopAppBar(
                    title = {
                        Text(
                            text = item?.name ?: "",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = if (scrollBehavior.isCollapsed) 1 else Int.MAX_VALUE,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = scrimColour,
                        scrolledContainerColor = scrimColour,
                    ),
                    scrollBehavior = scrollBehavior,
                )
            }

            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item?.rating?.let { Text(it) }

                        item?.productionYear?.let { Text(it.toString(10)) }

                        item?.runtime?.let {
                            val duration = it.toDuration(DurationUnit.MILLISECONDS)
                            val time = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                LocalTime.ofSecondOfDay(duration.inWholeSeconds)
                            } else {
                                TODO("VERSION.SDK_INT < O")
                            }

                            Text(time.toString())
                        }

                        item?.let {
                            IconButton(onClick = { onPlayPressed(it.id) }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "play")
                            }
                        }
                    }
                }

                item?.tagLines?.let {
                    if (it.isNotEmpty()) {
                        item {
                            Text(it.random())
                        }
                    }
                }

                item?.overview?.let {
                    item {
                        Text(it)
                    }
                }
            }
        }

        if (item == null) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}
