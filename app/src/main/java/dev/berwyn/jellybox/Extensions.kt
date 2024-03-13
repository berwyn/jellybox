package dev.berwyn.jellybox

import org.jellyfin.sdk.api.client.ApiClient

fun ApiClient.isReady(): Boolean = baseUrl != ""
    && userId != null
    && accessToken != null
