package dev.berwyn.jellybox.domain

import dev.berwyn.jellybox.data.local.JellyboxDatabase

class GetActiveServerUseCase(
    private val database: JellyboxDatabase
) {
    operator fun invoke() = database.serverDao().getSelectedFlow()
}
