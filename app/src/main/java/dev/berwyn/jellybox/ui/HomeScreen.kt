package dev.berwyn.jellybox.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import dev.berwyn.jellybox.data.ApplicationState

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onOnboardingRequested: () -> Unit = {},
) {
    val serverName by viewModel.selectedServerName.collectAsState(initial = null)
    val viewsState by viewModel.viewsState.collectAsState(initial = ViewsState.Loading)

    Column {
        when (viewsState) {
            is ViewsState.Loading -> CircularProgressIndicator()
            is ViewsState.Error -> Text("Load failed: ${(viewsState as ViewsState.Error).reason.localizedMessage}")
            is ViewsState.NotConfigured -> Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Looks like you haven't configured any servers!",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )
                Button(onClick = onOnboardingRequested) {
                    Text("Get Started")
                }
            }
            is ViewsState.Success -> Column {
                Text("Connected to $serverName")
                (viewsState as ViewsState.Success).views.forEach { view ->
                    Text(view.name ?: "unknown view")
                }
            }
        }
    }
}
