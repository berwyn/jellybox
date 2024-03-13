package dev.berwyn.jellybox.ui.locals

import androidx.compose.runtime.compositionLocalOf
import org.jellyfin.sdk.api.client.ApiClient

val LocalJellyfinClient = compositionLocalOf<ApiClient> {
    error("`LocalJellyfinClient` not set!")
}
