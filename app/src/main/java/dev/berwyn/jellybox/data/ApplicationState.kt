package dev.berwyn.jellybox.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.jellyfin.sdk.api.client.ApiClient

class ApplicationState {
    var jellyfinClient: ApiClient? by mutableStateOf(null)
        private set

    fun setSelectedClient(client: ApiClient?) {
        jellyfinClient = client
    }
}
