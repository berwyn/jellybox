package dev.berwyn.jellybox.domain

import android.content.Context
import dev.berwyn.jellybox.data.local.Server
import dev.berwyn.jellybox.getPackageInfo
import org.jellyfin.sdk.BuildConfig
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.android.androidDevice
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.HttpClientOptions
import org.jellyfin.sdk.api.client.extensions.sessionApi
import org.jellyfin.sdk.api.client.extensions.userApi
import org.jellyfin.sdk.model.ClientInfo
import org.jellyfin.sdk.model.DeviceInfo

class CreateClientUseCase(
    private val retrieveServerCredential: RetrieveServerCredentialUseCase,
    private val jellyfin: Jellyfin,
    private val context: Context,
) {
    operator fun invoke(server: Server): ApiClient {
        val credential = retrieveServerCredential(server)

        return jellyfin.createApi(
            baseUrl = server.url.toString(),
            accessToken = credential,
            clientInfo = ClientInfo(
                name = "Jellybox",
                version = context.getPackageInfo().versionName
            ),
            deviceInfo = androidDevice(context),
        )
    }

    // TODO: figure out better overload
    suspend operator fun invoke(server: Server, validateSession: Boolean = true): ApiClient {
        val client = this(server)

        val user by client.userApi.getCurrentUser()
        client.userId = user.id

        return client
    }
}
