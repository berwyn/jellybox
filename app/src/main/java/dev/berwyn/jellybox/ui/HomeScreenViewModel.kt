package dev.berwyn.jellybox.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.berwyn.jellybox.domain.GetActiveServerUseCase
import dev.berwyn.jellybox.domain.RetrieveServerCredentialUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.extensions.userApi
import org.jellyfin.sdk.api.client.extensions.userViewsApi
import org.jellyfin.sdk.model.api.BaseItemDto
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    getActiveServer: GetActiveServerUseCase,
    private val retrieveServerCredential: RetrieveServerCredentialUseCase,
    private val jellyfin: Jellyfin,
) : ViewModel() {
    private val activeServer = getActiveServer()
    private val client = activeServer
        .distinctUntilChanged()
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
            .takeWhile { it != null }
            .combine(user.takeWhile { it != null }) { client, user ->
                client!!.userViewsApi.getUserViews(user!!.id).content.items
            }
            .takeWhile { it != null }
            .catch { viewsStateFlow.emit(ViewsState.Error(it)) }
            .collect { viewsStateFlow.emit(ViewsState.Success(it!!)) }
        }
    }
}

sealed class ViewsState {
    object Loading : ViewsState()
    data class Success(val views: List<BaseItemDto>) : ViewsState()
    data class Error(val reason: Throwable) : ViewsState()
}
