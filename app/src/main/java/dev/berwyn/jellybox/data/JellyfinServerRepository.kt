package dev.berwyn.jellybox.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import dev.berwyn.jellybox.data.local.Jellybox
import dev.berwyn.jellybox.data.local.Server
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface JellyfinServerRepository {
    fun getServers(): Flow<ImmutableList<Server>>
    fun getSelectedServer(): Flow<Server?>
}

class DatabaseJellyfinServerRepository(
    private val database: Jellybox,
): JellyfinServerRepository {
    override fun getServers() = database.serverQueries
        .getAll()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { it.toImmutableList() }

    override fun getSelectedServer() = database.serverQueries
        .getSelectedServer()
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
}
