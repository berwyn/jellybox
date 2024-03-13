package dev.berwyn.jellybox.domain

import dev.berwyn.jellybox.data.local.Server
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.extensions.sessionApi
import org.jellyfin.sdk.api.client.extensions.userApi

class CreateClientUseCase(
    private val retrieveServerCredential: RetrieveServerCredentialUseCase,
    private val jellyfin: Jellyfin,
) {
    operator fun invoke(server: Server): ApiClient {
        val credential = retrieveServerCredential(server)

        return jellyfin.createApi(server.url.toString(), accessToken = credential)
    }

    // TODO: figure out better overload
    suspend operator fun invoke(server: Server, validateSession: Boolean = true): ApiClient {
        val client = this(server)

        val user by client.userApi.getCurrentUser()
        client.userId = user.id

        return client
    }
}
