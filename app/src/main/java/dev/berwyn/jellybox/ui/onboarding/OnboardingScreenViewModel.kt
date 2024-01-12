package dev.berwyn.jellybox.ui.onboarding

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.domain.StoreServerCredentialRetention
import dev.berwyn.jellybox.domain.StoreServerUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.exception.InvalidStatusException
import org.jellyfin.sdk.api.client.extensions.systemApi
import org.jellyfin.sdk.api.client.extensions.userApi
import org.jellyfin.sdk.discovery.RecommendedServerInfoScore
import org.jellyfin.sdk.model.api.AuthenticateUserByName
import org.jellyfin.sdk.model.api.ServerDiscoveryInfo

class OnboardingScreenViewModel(
    private val jellyfin: Jellyfin,
    private val appState: ApplicationState,
    private val storeServer: StoreServerUseCase,
) : ViewModel() {
    var serverAddress by mutableStateOf("")
        private set

    var localServers: List<ServerDiscoveryInfo> by mutableStateOf(persistentListOf())
        private set

    var retainCredentials by mutableStateOf(false)

    var loading by mutableStateOf(false)
        private set

    var hasError by mutableStateOf(false)
        private set

    val hideNavigation = appState::hideNavigation
    val restoreNavigation = appState::showNavigation

    fun discoverLocalServers() {
        viewModelScope.launch {
            // For whatever reason, this call doesn't move itself onto the IO dispatcher, so we have to do it manually
            withContext(Dispatchers.IO) {
                localServers = jellyfin.discovery.discoverLocalServers().toList()
            }
        }
    }

    fun checkServerAddress(address: String) {
        loading = true
        viewModelScope.launch {
            try {
                val servers =
                    jellyfin.discovery.getRecommendedServers(address, RecommendedServerInfoScore.GOOD)

                serverAddress = if (servers.isNotEmpty()) {
                    servers.first().address
                } else {
                    ""
                }
            } catch (iae: IllegalArgumentException) {
                // Do nothing, URL wasn't valid
            } finally {
                loading = false
            }
        }
    }

    fun login(username: String, password: String, onSuccess: () -> Unit = {}, onFailure: () -> Unit = {}) {
        loading = true
        hasError = false

        viewModelScope.launch {
            val api = jellyfin.createApi(baseUrl = serverAddress)

            try {
                val systemInfo by api.systemApi.getPublicSystemInfo()
                val result by api.userApi.authenticateUserByName(
                    AuthenticateUserByName(username = username, pw = password)
                )

                api.accessToken = result.accessToken

                storeServer(
                    name = systemInfo.serverName ?: serverAddress,
                    uri = serverAddress,
                    authToken = result.accessToken!!,
                    credentialRetention = if (retainCredentials) {
                        StoreServerCredentialRetention.RETAIN
                    } else {
                        StoreServerCredentialRetention.NONE
                    }
                )
                    .onSuccess {
                        loading = false

                        onSuccess()
                    }
                    .onFailure {
                        loading = false
                        hasError = true

                        onFailure()
                    }
            } catch (err: InvalidStatusException) {
                // TODO: Handle properly
                Log.d("OnboardingScreenViewModel", "Failed to login", err)
                onFailure()
            }

        }
    }
}
