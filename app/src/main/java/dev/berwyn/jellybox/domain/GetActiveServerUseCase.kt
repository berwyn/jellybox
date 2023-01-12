package dev.berwyn.jellybox.domain

import dev.berwyn.jellybox.data.local.JellyboxDatabase
import javax.inject.Inject

class GetActiveServerUseCase @Inject constructor(
    private val database: JellyboxDatabase
) {
    operator fun invoke() = database.serverDao().getSelectedFlow()
}
