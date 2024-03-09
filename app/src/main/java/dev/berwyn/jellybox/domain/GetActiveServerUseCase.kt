package dev.berwyn.jellybox.domain

import dev.berwyn.jellybox.data.JellyfinServerRepository

class GetActiveServerUseCase(
    private val serverRepository: JellyfinServerRepository
) {
    operator fun invoke() = serverRepository.getSelectedServer()
}
