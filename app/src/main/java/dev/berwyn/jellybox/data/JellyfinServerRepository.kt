package dev.berwyn.jellybox.data

import dev.berwyn.jellybox.data.local.JellyboxDatabase
import dev.berwyn.jellybox.data.local.JellyfinServer
import kotlinx.coroutines.flow.Flow

interface JellyfinServerRepository {
    fun getServers(): Flow<List<JellyfinServer>>
    fun getSelectedServer(): Flow<JellyfinServer?>
}

class DatabaseJellyfinServerRepository(
    private val database: JellyboxDatabase,
): JellyfinServerRepository {
    override fun getServers(): Flow<List<JellyfinServer>> = database.serverDao().getAll()
    override fun getSelectedServer() = database.serverDao().getSelectedFlow()
}
