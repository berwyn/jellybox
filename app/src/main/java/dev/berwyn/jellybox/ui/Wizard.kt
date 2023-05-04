package dev.berwyn.jellybox.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.berwyn.jellybox.ui.previews.DynamicColourPreviews
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class WizardPage(
    val name: String,
    val component: @Composable WizardScope.() -> Unit,
)

class WizardBuilder {
    internal var pages: MutableList<WizardPage> = mutableListOf()

    fun page(name: String, page: @Composable WizardScope.() -> Unit) {
        pages.add(WizardPage(name, page))
    }
}

fun buildWizardPageList(builder: WizardBuilder, pageFactory: WizardBuilder.() -> Unit): ImmutableList<WizardPage> {
    pageFactory(builder)

    return builder.pages.toImmutableList()
}

@OptIn(ExperimentalFoundationApi::class)
class WizardScope(
    private val commandScope: CoroutineScope,
    private val pagerState: PagerState,
) {
    fun goToNextPage() {
        if (!pagerState.canScrollForward) {
            return
        }

        commandScope.launch {
            pagerState.scrollToPage(pagerState.currentPage + 1)
        }
    }

    fun goToPreviousPage() {
        if (!pagerState.canScrollBackward) {
            return
        }

        commandScope.launch {
            pagerState.scrollToPage(pagerState.currentPage - 1)
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun HorizontalPagerIndicator(
    pageCount: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(pageCount) { index ->
            val color = if (pagerState.currentPage == index) activeColor else inactiveColor

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .aspectRatio(1.0f)
                    .drawWithContent {
                        drawCircle(color)
                    },
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun Wizard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    activeColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    inactiveColor: Color = MaterialTheme.colorScheme.secondary,
    builder: WizardBuilder.() -> Unit
) {
    val pagerState = rememberPagerState()
    val commandScope = rememberCoroutineScope()
    val componentScope = remember(pagerState, commandScope) {
        WizardScope(commandScope, pagerState)
    }

    val pages = remember(builder) {
        buildWizardPageList(WizardBuilder(), builder)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        HorizontalPager(
            pageCount = pages.size,
            state = pagerState,
            key = { page -> pages[page].name },
            userScrollEnabled = false,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { page ->
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                pages[page].component(componentScope)
            }
        }

        HorizontalPagerIndicator(
            pageCount = pages.size,
            pagerState = pagerState,
            activeColor = activeColor,
            inactiveColor = inactiveColor,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )
    }
}

@Composable
@ThemePreviews
@DynamicColourPreviews
private fun WizardPreview() {
    JellyboxTheme {
        Wizard {
            page("1") {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("First")

                    Button(onClick = { goToNextPage() }) {
                        Text("Next")
                    }
                }
            }

            page("2") {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Second")

                    Row {
                        Button(onClick = { goToPreviousPage() }) {
                            Text("Back")
                        }

                        Button(onClick = { goToNextPage() }) {
                            Text("Next")
                        }
                    }
                }
            }
            page("3") {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Third")

                    Button(onClick = { goToPreviousPage() }) {
                        Text("Back")
                    }
                }
            }
        }
    }
}
