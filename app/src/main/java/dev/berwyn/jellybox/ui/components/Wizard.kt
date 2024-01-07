package dev.berwyn.jellybox.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.berwyn.jellybox.ui.previews.DynamicColourPreviews
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

private data class WizardPage(
    val name: String,
    val component: @Composable WizardScope.() -> Unit,
)

interface WizardBuilder {
    fun page(name: String, page: @Composable WizardScope.() -> Unit)
}

private class WizardBuilderImpl : WizardBuilder {
    private var pages: MutableList<WizardPage> = mutableListOf()

    override fun page(name: String, page: @Composable WizardScope.() -> Unit) {
        pages.add(WizardPage(name, page))
    }

    fun build(): ImmutableList<WizardPage> = pages.toImmutableList()
}

interface WizardScope {
    fun goToNextPage()
    fun goToPreviousPage()
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun Wizard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    activeColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    inactiveColor: Color = MaterialTheme.colorScheme.tertiary,
    factory: WizardBuilder.() -> Unit
) {
    val pages = remember(factory) {
        WizardBuilderImpl().also(factory).build()
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { pages.size },
    )

    val commandScope = rememberCoroutineScope()

    val componentScope = remember(pagerState, commandScope) {
        object : WizardScope {
            override fun goToNextPage() {
                if (!pagerState.canScrollForward) {
                    return
                }

                commandScope.launch {
                    pagerState.scrollToPage(pagerState.currentPage + 1)
                }
            }

            override fun goToPreviousPage() {
                if (!pagerState.canScrollBackward) {
                    return
                }

                commandScope.launch {
                    pagerState.scrollToPage(pagerState.currentPage - 1)
                }
            }

        }
    }



    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        HorizontalPager(
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
