package dev.berwyn.jellybox.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.berwyn.jellybox.data.local.JellyboxDatabase
import kotlinx.coroutines.flow.map
import org.jellyfin.sdk.api.client.ApiClient
import javax.inject.Inject

class ApplicationState @Inject constructor(
    private val database: JellyboxDatabase,
) {
    var jellyfinClient: ApiClient? by mutableStateOf(null)
        private set

    val hasServersConfigured = database.serverDao()
        .getAll()
        .map { it.isNotEmpty() }

    fun setSelectedClient(client: ApiClient?) {
        jellyfinClient = client
    }
}
