package dev.berwyn.jellybox.domain

import dev.berwyn.jellybox.data.local.JellyboxDatabase
import dev.berwyn.jellybox.data.local.JellyfinServer

class SelectActiveServerUseCase(
    private val database: JellyboxDatabase,
) {
    suspend operator fun invoke(server: JellyfinServer? = null, useDefault: Boolean = false) {
        require(server != null || useDefault) { "Either `server` or `useDefault` must be provided" }

        val selectedServer = when {
            useDefault -> database.serverDao().getDefault()
            server != null -> server
            else -> throw IllegalArgumentException()
        }

        selectedServer
            ?.apply { isSelected = true }
            ?.let { database.serverDao().updateServer(it) }
    }
}
