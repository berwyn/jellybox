package dev.berwyn.jellybox.data

import org.jellyfin.sdk.api.client.ApiClient

data class ApplicationState(
    var jellyfinClient: ApiClient?
)
