package dev.berwyn.jellybox.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.domain.GetActiveServerUseCase
import dev.berwyn.jellybox.domain.RetrieveServerCredentialUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.extensions.userApi
import org.jellyfin.sdk.api.client.extensions.userViewsApi
import org.jellyfin.sdk.model.api.BaseItemDto

class HomeScreenViewModel(
    applicationState: ApplicationState,
    getActiveServer: GetActiveServerUseCase,
    private val retrieveServerCredential: RetrieveServerCredentialUseCase,
    private val jellyfin: Jellyfin,
) : ViewModel() {
    private val activeServer = getActiveServer()
    private val client = activeServer
        .distinctUntilChanged()
        .onEach { applicationState.selectedServer = it }
        .map {
            it?.let { jellyfin.createApi(it.uri, accessToken = retrieveServerCredential(it)) }
        }

    private val user = client
        .map { it?.userApi?.getCurrentUser()?.content }

    val selectedServerName: Flow<String?> = activeServer
        .map { it?.name }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 1)

    private val viewsStateFlow = MutableStateFlow<ViewsState>(ViewsState.Loading)
    val viewsState: Flow<ViewsState>
        get() = viewsStateFlow.asStateFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ViewsState.Loading)

    init {
        viewModelScope.launch {
            client
                .onEach { viewsStateFlow.emit(ViewsState.Loading) }
                .combine(user) { client, user ->
                    client ?: return@combine null
                    user ?: return@combine null

                    client.userViewsApi.getUserViews(user.id).content.items
                }
                .catch { viewsStateFlow.emit(ViewsState.Error(it)) }
                .collect {
                    if (it != null)
                        viewsStateFlow.emit(ViewsState.Success(it))
                    else
                        viewsStateFlow.emit(ViewsState.NotConfigured)
                }
        }
    }
}

sealed class ViewsState {
    object Loading : ViewsState()
    object NotConfigured : ViewsState()
    data class Success(val views: List<BaseItemDto>) : ViewsState()
    data class Error(val reason: Throwable) : ViewsState()
}
