package dev.berwyn.jellybox.data

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.berwyn.jellybox.data.local.JellyfinServer
import dev.berwyn.jellybox.domain.RetrieveServerCredentialUseCase
import dev.berwyn.jellybox.ui.navigation.NavigationType
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.extensions.userApi
import org.jellyfin.sdk.api.client.ApiClient as JellyfinApi

class ApplicationState(
    jellyfin: Jellyfin,
    retrieveServerCredential: RetrieveServerCredentialUseCase,
) {
    var navigationType: NavigationType by mutableStateOf(NavigationType.Bar)
    var navigationHidden: Boolean by mutableStateOf(false)

    var selectedServer: JellyfinServer? by mutableStateOf(null)

    val hasActiveServer: Boolean by derivedStateOf {
        selectedServer != null
    }

    val jellyfinClient: JellyfinApi? by derivedStateOf {
        selectedServer?.let { server ->
            jellyfin.createApi(server.uri, accessToken = retrieveServerCredential(server))
        }
    }

    suspend fun ensureSession() = Result.runCatching {
        val client = jellyfinClient

        client ?: error("No active server")

        val user by client.userApi.getCurrentUser()

        client.userId = user.id
        client
    }
}
