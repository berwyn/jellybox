package dev.berwyn.jellybox.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.berwyn.jellybox.data.ApplicationState
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val appState: ApplicationState,
) : ViewModel() {
    val selectedServerName: String? by derivedStateOf {
        appState.selectedServer?.name
    }
}
