package dev.berwyn.jellybox.ui.locals

import androidx.compose.runtime.compositionLocalOf
import org.jellyfin.sdk.api.client.ApiClient

sealed class JellyfinClientState {
    data object Unset : JellyfinClientState()
    data class Configured(val client: ApiClient) : JellyfinClientState()
}

val LocalJellyfinClientState = compositionLocalOf<JellyfinClientState> {
    JellyfinClientState.Unset
}
