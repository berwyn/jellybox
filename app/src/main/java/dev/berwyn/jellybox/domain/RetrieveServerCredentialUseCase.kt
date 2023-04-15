package dev.berwyn.jellybox.domain

import android.content.SharedPreferences
import dev.berwyn.jellybox.data.local.JellyfinServer

class RetrieveServerCredentialUseCase(
    private val securePrefs: SharedPreferences
) {
    operator fun invoke(server: JellyfinServer) = securePrefs.getString("server/${server.uri}", null)
}
