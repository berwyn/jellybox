package dev.berwyn.jellybox.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import cafe.adriel.lyricist.ProvideStrings
import dev.berwyn.jellybox.data.local.Jellybox
import dev.berwyn.jellybox.domain.RetrieveServerCredentialUseCase
import dev.berwyn.jellybox.ui.locals.LocalJellyfinClient
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.exception.InvalidStatusException
import org.jellyfin.sdk.api.client.extensions.userApi
import org.koin.compose.koinInject

@Composable
fun JellyboxApp(
    jellybox: Jellybox = koinInject(),
    jellyfin: Jellyfin = koinInject(),
    retrieveServerCredential: RetrieveServerCredentialUseCase = koinInject(),
) {
    val jellyfinClient = jellyfin.createApi("")

    fun resetClient() {
        jellyfinClient.baseUrl = ""
        jellyfinClient.userId = null
        jellyfinClient.accessToken = null
    }

    LaunchedEffect(Unit) {
        jellybox.serverQueries.getSelectedServer()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .collect { server ->
                if (server == null) {
                    resetClient()
                } else {
                    jellyfinClient.baseUrl = server.url.toString()
                    jellyfinClient.accessToken = retrieveServerCredential(server)

                    try {
                        val user by jellyfinClient.userApi.getCurrentUser()
                        jellyfinClient.userId = user.id
                    } catch (e: InvalidStatusException) {
                        resetClient()
                    }
                }
            }
    }

    CompositionLocalProvider(LocalJellyfinClient provides jellyfinClient) {
        ProvideStrings {
            JellyboxTheme {
                JellyboxNavigation()
            }
        }
    }
}
