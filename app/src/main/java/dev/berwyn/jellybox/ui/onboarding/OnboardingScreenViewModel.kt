package dev.berwyn.jellybox.ui.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.discovery.RecommendedServerInfoScore
import org.jellyfin.sdk.model.api.ServerDiscoveryInfo
import javax.inject.Inject

@HiltViewModel
class OnboardingScreenViewModel @Inject constructor(
    private val jellyfin: Jellyfin
) : ViewModel() {
    var serverAddress by mutableStateOf("")
        private set

    var localServers: List<ServerDiscoveryInfo> by mutableStateOf(persistentListOf())
        private set

    var loading by mutableStateOf(false)
        private set

    fun discoverLocalServers() {
        viewModelScope.launch {
            jellyfin.discovery.discoverLocalServers()
                .collect { localServers = localServers + it }
        }
    }

    fun checkServerAddress(address: String) {
        loading = true
        viewModelScope.launch {
            val servers =
                jellyfin.discovery.getRecommendedServers(address, RecommendedServerInfoScore.GOOD)

            serverAddress = if (servers.isNotEmpty()) {
                servers.first().address
            } else {
                ""
            }

            loading = false
        }
    }
}