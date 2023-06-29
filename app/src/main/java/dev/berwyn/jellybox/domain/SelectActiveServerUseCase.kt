package dev.berwyn.jellybox.domain

import android.util.Log
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.data.local.JellyboxDatabase
import dev.berwyn.jellybox.data.local.JellyfinServer
import org.jellyfin.sdk.api.client.extensions.userApi

interface SelectActiveServerUseCase {
    suspend operator fun invoke(server: JellyfinServer? = null, useDefault: Boolean = false)
}

class DatabaseSelectActiveServerUseCase(
    private val database: JellyboxDatabase,
    private val applicationState: ApplicationState,
) : SelectActiveServerUseCase {
    override suspend operator fun invoke(server: JellyfinServer?, useDefault: Boolean) {
        require(server != null || useDefault) { "Either `server` or `useDefault` must be provided" }

        val selectedServer = when {
            useDefault -> database.serverDao().getDefault()
            server != null -> server
            else -> throw IllegalArgumentException()
        }

        selectedServer
            ?.apply { isSelected = true }
            ?.let {
                database.serverDao().updateServer(it)
                applicationState.selectedServer = it

                applicationState.jellyfinClient?.let { client ->
                    try {
                        val user by client.userApi.getCurrentUser()

                        client.userId = user.id

                        Log.d(::DatabaseSelectActiveServerUseCase.name, "Logged in as user ${user.name}")
                    } catch (err: Error) {
                        Log.e(::DatabaseSelectActiveServerUseCase.name, "Failed to ping system", err)
                    }

                }
            }
    }
}
