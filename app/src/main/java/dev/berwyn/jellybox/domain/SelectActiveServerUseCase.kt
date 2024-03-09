package dev.berwyn.jellybox.domain

import dev.berwyn.jellybox.data.local.Jellybox
import dev.berwyn.jellybox.data.local.Server

interface SelectActiveServerUseCase {
    suspend operator fun invoke(server: Server? = null, useDefault: Boolean = false)
}

class DatabaseSelectActiveServerUseCase(
    private val database: Jellybox,
) : SelectActiveServerUseCase {
    override suspend operator fun invoke(server: Server?, useDefault: Boolean) {
        when {
            useDefault -> {
                val default = database.serverQueries.getDefaultServer().executeAsOne()

                database.serverQueries.setSelected(default.id)
            }
            server != null -> {
                val current = database.serverQueries.getSelectedServer().executeAsOneOrNull()

                if (current?.id == server.id) {
                    database.serverQueries.deselectAll()
                } else {
                    database.serverQueries.setSelected(server.id)
                }
            }
            else -> {
                database.serverQueries.deselectAll()
            }
        }
    }
}
