package dev.berwyn.jellybox.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.berwyn.jellybox.data.JellyfinServerRepository
import dev.berwyn.jellybox.domain.SelectActiveServerUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class JellyboxAppViewModel(
    val selectActiveServer: SelectActiveServerUseCase,
    serverRepository: JellyfinServerRepository,
): ViewModel() {
    val servers = serverRepository.getServers()
        .map { it.toImmutableList() }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 1)
}
