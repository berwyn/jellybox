package dev.berwyn.jellybox.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.google.accompanist.pager.*
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
    var pages: MutableList<WizardPage> = mutableListOf()

    fun page(name: String, page: @Composable WizardScope.() -> Unit) {
        pages.add(WizardPage(name, page))
    }
}

fun buildWizardPageList(builder: WizardBuilder, pageFactory: WizardBuilder.() -> Unit): ImmutableList<WizardPage> {
    pageFactory(builder)

    return builder.pages.toImmutableList()
}

@OptIn(ExperimentalPagerApi::class)
class WizardScope(
    private val commandScope: CoroutineScope,
    private val pagerState: PagerState,
) {
    fun goToNextPage() {
        val nextPage = pagerState.currentPage + 1

        if (nextPage >= pagerState.pageCount) {
            return
        }

        commandScope.launch {
            pagerState.scrollToPage(nextPage)
        }
    }

    fun goToPreviousPage() {
        val prevPage = pagerState.currentPage - 1

        if (prevPage < 0) {
            return
        }

        commandScope.launch {
            pagerState.scrollToPage(prevPage)
        }
    }
}

@Composable
@OptIn(ExperimentalPagerApi::class)
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
            count = pages.size,
            state = pagerState,
            key = { page -> pages[page].name },
            userScrollEnabled = false,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { page ->
            pages[page].component(componentScope)
        }

        HorizontalPagerIndicator(
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
fun WizardPreview() {
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
