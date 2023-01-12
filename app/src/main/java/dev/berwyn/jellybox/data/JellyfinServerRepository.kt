package dev.berwyn.jellybox.data

import dev.berwyn.jellybox.data.local.JellyboxDatabase
import dev.berwyn.jellybox.data.local.JellyfinServer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface JellyfinServerRepository {
    fun getServers(): Flow<List<JellyfinServer>>
    fun getSelectedServer(): Flow<JellyfinServer?>
}

@Singleton
class DatabaseJellyfinServerRepository @Inject constructor(
    private val database: JellyboxDatabase,
): JellyfinServerRepository {
    override fun getServers(): Flow<List<JellyfinServer>> = database.serverDao().getAll()
    override fun getSelectedServer() = database.serverDao().getSelectedFlow()
}
