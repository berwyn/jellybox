package dev.berwyn.jellybox.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.berwyn.jellybox.data.JellyfinServerRepository
import dev.berwyn.jellybox.domain.SelectActiveServerUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class JellyboxAppViewModel @Inject constructor(
    val selectActiveServer: SelectActiveServerUseCase,
    serverRepository: JellyfinServerRepository,
): ViewModel() {
    val servers = serverRepository.getServers()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 1)
}
