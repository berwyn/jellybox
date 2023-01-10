package dev.berwyn.jellybox.domain

import android.content.SharedPreferences
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.data.local.JellyboxDatabase
import dev.berwyn.jellybox.data.local.JellyfinServer
import dev.berwyn.jellybox.security.SecurePrefs
import javax.inject.Inject

class SelectActiveServerUseCase @Inject constructor(
    private val database: JellyboxDatabase,
    private val appState: ApplicationState,
    @SecurePrefs private val securePrefs: SharedPreferences,
) {
    suspend operator fun invoke(server: JellyfinServer? = null, useDefault: Boolean = false) {
        require(server != null || useDefault) { "Either `server` or `useDefault` must be provided" }

        val selectedServer = when {
            useDefault -> database.serverDao().getDefault()
            server != null -> server
            else -> throw IllegalArgumentException()
        }

        selectedServer ?: return

        appState.selectedServer = selectedServer
    }
}
