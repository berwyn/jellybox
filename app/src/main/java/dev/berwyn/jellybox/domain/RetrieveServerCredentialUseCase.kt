package dev.berwyn.jellybox.domain

import android.content.SharedPreferences
import dev.berwyn.jellybox.data.local.Server

class RetrieveServerCredentialUseCase(
    private val securePrefs: SharedPreferences
) {
    operator fun invoke(server: Server) = securePrefs.getString("server/${server.url}", null)
}
