package dev.berwyn.jellybox.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dev.berwyn.jellybox.data.ApplicationState

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    Column() {
        Text("Home")
        Text("Connected to ${viewModel.selectedServerName}")
    }
}
