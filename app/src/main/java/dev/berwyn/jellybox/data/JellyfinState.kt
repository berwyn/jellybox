package dev.berwyn.jellybox.data

import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.*
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.ApiClient
import javax.inject.Inject

data class JellyfinServer(
    val baseUrl: String,
    val name: String,
)

interface JellyfinState {
    /**
     * A flow containing an immutable list of servers that the application is aware of.
     */
    val servers: Flow<ImmutableList<JellyfinServer>>

    /**
     * A flow containing the currently-selected server, or null if there is no server selected.
     */
    val selectedServer: Flow<JellyfinServer?>

    /**
     * A flow containing the client to be used when communicating with the current server.
     */
    val apiClient: Flow<ApiClient?>

    suspend fun selectServer(server: JellyfinServer)
}

class DefaultJellyfinState @Inject constructor(private val jellyfin: Jellyfin) : JellyfinState {
    private val serversFlow = MutableSharedFlow<ImmutableList<JellyfinServer>>(replay = 1)
    override val servers: Flow<ImmutableList<JellyfinServer>>
        get() = serversFlow.asSharedFlow()

    private val selectedServerFlow = MutableSharedFlow<JellyfinServer?>(replay = 1)
    override val selectedServer: Flow<JellyfinServer?>
        get() = selectedServerFlow.asSharedFlow()

    private val apiClientFlow = MutableSharedFlow<ApiClient?>(replay = 1)
    override val apiClient: Flow<ApiClient?>
        get() = selectedServer
            .distinctUntilChanged()
            .map { server ->
                if (server == null) {
                    null
                } else {
                    jellyfin.createApi(baseUrl = server.baseUrl)
                }
            }

    override suspend fun selectServer(server: JellyfinServer) {
        selectedServerFlow.emit(server)
    }
}