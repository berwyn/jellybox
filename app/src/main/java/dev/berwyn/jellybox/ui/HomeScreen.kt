package dev.berwyn.jellybox.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import dev.berwyn.jellybox.data.ApplicationState

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val serverName by viewModel.selectedServerName.collectAsState(initial = null)
    val viewsState by viewModel.viewsState.collectAsState(initial = ViewsState.Loading)

    Column {
        Text("Home")
        Text("Connected to $serverName")

        when (viewsState) {
            is ViewsState.Loading -> CircularProgressIndicator()
            is ViewsState.Error -> Text("Load failed: ${(viewsState as ViewsState.Error).reason.localizedMessage}")
            is ViewsState.Success -> (viewsState as ViewsState.Success).views.forEach { view ->
                Text(view.name ?: "unknown view")
            }
        }
    }
}
