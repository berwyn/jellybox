package dev.berwyn.jellybox.domain

import android.content.Context
import dev.berwyn.jellybox.data.local.Server
import dev.berwyn.jellybox.getPackageInfo
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.android.androidDevice
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.extensions.userApi
import org.jellyfin.sdk.model.ClientInfo

class CreateClientUseCase(
    private val retrieveServerCredential: RetrieveServerCredentialUseCase,
    private val jellyfin: Jellyfin,
    private val context: Context,
) {
    companion object {
        private val cache = HashMap<Server, ApiClient>()
    }

    operator fun invoke(server: Server): ApiClient {
        return cache.getOrPut(server) {
            val credential = retrieveServerCredential(server)

            jellyfin.createApi(
                baseUrl = server.url.toString(),
                accessToken = credential,
                clientInfo = ClientInfo(
                    name = "Jellybox",
                    version = context.getPackageInfo().versionName
                ),
                deviceInfo = androidDevice(context),
            )
        }
    }

    // TODO: figure out better overload
    suspend operator fun invoke(server: Server, validateSession: Boolean = true): ApiClient {
        val client = this(server)

        if (client.userId == null) {
            val user by client.userApi.getCurrentUser()
            client.userId = user.id
        }

        return client
    }
}
