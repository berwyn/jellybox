package dev.berwyn.jellybox.ui.onboarding

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.berwyn.jellybox.data.ApplicationState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.exception.InvalidStatusException
import org.jellyfin.sdk.api.client.extensions.userApi
import org.jellyfin.sdk.discovery.RecommendedServerInfoScore
import org.jellyfin.sdk.model.api.AuthenticateUserByName
import org.jellyfin.sdk.model.api.ServerDiscoveryInfo
import javax.inject.Inject

@HiltViewModel
class OnboardingScreenViewModel @Inject constructor(
    private val jellyfin: Jellyfin,
    private val appState: ApplicationState,
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

    fun login(username: String, password: String, onSuccess: () -> Unit = {}, onFailure: () -> Unit = {}) {
        loading = true
        viewModelScope.launch {
            val api = jellyfin.createApi(baseUrl = serverAddress)
            try {
                val result by api.userApi.authenticateUserByName(
                    AuthenticateUserByName(username = username, pw = password)
                )

                api.accessToken = result.accessToken

                appState.jellyfinClient = api

                loading = false

                onSuccess()
            } catch (err: InvalidStatusException) {
                // TODO: Handle properly
                Log.d("OnboardingScreenViewModel", "Failed to login", err)
                onFailure()
            }

        }
    }
}
