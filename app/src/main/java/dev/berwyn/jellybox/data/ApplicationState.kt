package dev.berwyn.jellybox.data

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import dev.berwyn.jellybox.data.local.JellyboxDatabase
import dev.berwyn.jellybox.data.local.JellyfinServer
import dev.berwyn.jellybox.domain.RetrieveServerCredentialUseCase
import dev.berwyn.jellybox.ui.navigation.NavigationType
import kotlinx.coroutines.flow.map
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.ApiClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationState @Inject constructor(
    database: JellyboxDatabase,
    jellyfin: Jellyfin,
    private val retrieveServerCredential: RetrieveServerCredentialUseCase
) {
    var navigationType: NavigationType by mutableStateOf(NavigationType.Bar)

    var selectedServer: JellyfinServer? by mutableStateOf(null)

    val jellyfinClient: ApiClient? by derivedStateOf {
        when (selectedServer) {
            is JellyfinServer -> jellyfin.createApi(selectedServer!!.uri).apply {
                accessToken = retrieveServerCredential(selectedServer!!)
            }
            else -> null
        }
    }

    val savedServers = database.serverDao()
        .getAll()

    val hasServersConfigured = savedServers
        .map { it.isNotEmpty() }
}
