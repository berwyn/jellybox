package dev.berwyn.jellybox.data

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.berwyn.jellybox.domain.RetrieveServerCredentialUseCase
import dev.berwyn.jellybox.ui.navigation.NavigationType
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.extensions.userApi

class ApplicationState(
    jellyfin: Jellyfin,
    retrieveServerCredential: RetrieveServerCredentialUseCase,
) {
    var navigationType: NavigationType by mutableStateOf(NavigationType.Bar)
    var navigationHidden: Boolean by mutableStateOf(false)
        private set

    var selectedMediaItem: String? by mutableStateOf(null)
        private set

    val isVideoSelected: Boolean by derivedStateOf {
        if (selectedMediaItem == null) {
            false
        } else {
            true // TODO: Actually check this
        }
    }

    suspend fun ensureSession() = Result.runCatching {
        error("Not implemented")
    }

    fun hideNavigation() {
        navigationHidden = true
    }

    fun showNavigation() {
        navigationHidden = false
    }
}
