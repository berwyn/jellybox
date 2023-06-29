package dev.berwyn.jellybox.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.berwyn.jellybox.ui.previews.DevicePreviews
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import org.koin.compose.koinInject
import java.security.InvalidParameterException

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = koinInject(),
    onOnboardingRequested: () -> Unit = {},
) {
    val serverName by viewModel.selectedServerName.collectAsState(initial = null)
    val viewsState by viewModel.viewsState.collectAsState(initial = ViewsState.Loading)

    HomeScreen(
        serverName = serverName,
        state = viewsState,
        onOnboardingRequested = onOnboardingRequested
    )
}

@Composable
private fun HomeScreen(
    serverName: String?,
    state: ViewsState,
    onOnboardingRequested: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            is ViewsState.Loading -> CircularProgressIndicator()
            is ViewsState.Error -> Text("Load failed: ${state.reason.localizedMessage}")
            is ViewsState.NotConfigured -> Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    "Looks like you haven't configured any servers!",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onOnboardingRequested) {
                    Text("Get Started")
                }
            }

            is ViewsState.Success -> Column {
                Text("Connected to $serverName")
                state.views.forEach { view ->
                    Text(view.name ?: "unknown view")
                }
            }
        }
    }
}

@ThemePreviews
@DevicePreviews
@Composable
private fun HomeScreenPreview(
    @PreviewParameter(ViewsStatePreviewProvider::class) viewsState: ViewsState,
) {
    JellyboxTheme {
        HomeScreen(
            serverName = "Test Server",
            state = viewsState,
            onOnboardingRequested = {},
        )
    }
}

private class ViewsStatePreviewProvider : PreviewParameterProvider<ViewsState> {
    override val values: Sequence<ViewsState> = sequenceOf(
        ViewsState.Loading,
        ViewsState.NotConfigured,
        ViewsState.Error(InvalidParameterException()),
        ViewsState.Success(listOf())
    )
}
