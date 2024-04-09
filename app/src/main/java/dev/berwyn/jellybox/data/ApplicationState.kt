package dev.berwyn.jellybox.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.berwyn.jellybox.ui.navigation.NavigationType
import org.jellyfin.sdk.api.client.ApiClient

class ApplicationState {
    var currentClient: ApiClient? by mutableStateOf(null)

    var navigationType: NavigationType by mutableStateOf(NavigationType.Bar)
    var navigationHidden: Boolean by mutableStateOf(false)
        private set

    var selectedMediaItem: String? by mutableStateOf(null)
        private set

    fun hideNavigation() {
        navigationHidden = true
    }

    fun showNavigation() {
        navigationHidden = false
    }
}
