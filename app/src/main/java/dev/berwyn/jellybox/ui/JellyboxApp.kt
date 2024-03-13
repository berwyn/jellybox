package dev.berwyn.jellybox.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import cafe.adriel.lyricist.ProvideStrings
import dev.berwyn.jellybox.data.local.Jellybox
import dev.berwyn.jellybox.domain.RetrieveServerCredentialUseCase
import dev.berwyn.jellybox.ui.locals.JellyfinClientState
import dev.berwyn.jellybox.ui.locals.LocalJellyfinClientState
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.coroutines.Dispatchers
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
    var jellyfinClientState: JellyfinClientState by remember {
        mutableStateOf(JellyfinClientState.Unset)
    }

    LaunchedEffect(Unit) {
        jellybox.serverQueries.getSelectedServer()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .collect { server ->
                if (server == null) {
                    jellyfinClientState = JellyfinClientState.Unset
                } else {
                    val client = jellyfin.createApi(
                        baseUrl = server.url.toString(),
                        accessToken = retrieveServerCredential(server),
                    )

                    try {
                        val user by client.userApi.getCurrentUser()
                        client.userId = user.id

                        jellyfinClientState = JellyfinClientState.Configured(client)
                    } catch (e: InvalidStatusException) {
                        jellyfinClientState = JellyfinClientState.Unset
                    }
                }
            }
    }

    CompositionLocalProvider(LocalJellyfinClientState provides jellyfinClientState) {
        ProvideStrings {
            JellyboxTheme {
                JellyboxNavigation()
            }
        }
    }
}
