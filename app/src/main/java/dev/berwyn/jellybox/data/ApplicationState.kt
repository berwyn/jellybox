package dev.berwyn.jellybox.data

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.berwyn.jellybox.domain.RetrieveServerCredentialUseCase
import dev.berwyn.jellybox.ui.navigation.NavigationType
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.extensions.userApi

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
