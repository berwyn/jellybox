package dev.berwyn.jellybox.ui.data

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreReadResponseOrigin

private const val TAG = "DataLoader"

@Composable
fun <T> DataLoader(
    request: suspend () -> T,
    modifier: Modifier = Modifier,
    loadingView: @Composable () -> Unit = {
        CircularProgressIndicator()
    },
    content: @Composable (T?) -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var data: T? by remember { mutableStateOf(null) }

    LaunchedEffect(request) {
        data = request()
        isLoading = false
    }

    Box(modifier, contentAlignment = Alignment.Center) {
        if (isLoading) {
            loadingView()
        } else {
            content(data)
        }
    }
}

@Composable
fun <T> DataLoader(
    request: suspend () -> T,
    defaultValue: T,
    modifier: Modifier = Modifier,
    loadingView: @Composable () -> Unit = {
        CircularProgressIndicator()
    },
    content: @Composable (T) -> Unit,
) = DataLoader(
    request = request,
    modifier = modifier,
    loadingView = loadingView,
    content = { content(it ?: defaultValue) },
)

@Composable
fun <Key : Any, T : Any> DataLoader(
    id: Key,
    store: Store<Key, T>,
    modifier: Modifier = Modifier,
    refreshData: Boolean = true,
    loadingView: @Composable () -> Unit = {
        CircularProgressIndicator()
    },
    content: @Composable (T?) -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var data: T? by remember { mutableStateOf(null) }

    LaunchedEffect(id, store) {
        store.stream(StoreReadRequest.cached(id, refreshData)).collect { response ->
            if (response.origin is StoreReadResponseOrigin.Fetcher) {
                isLoading = false
            }

            when (response) {
                is StoreReadResponse.Loading -> isLoading = true
                is StoreReadResponse.Data -> {
                    data = response.value
                }

                is StoreReadResponse.Error.Exception -> {
                    Log.e(TAG, "Error during data load", response.error)
                    TODO("Show error")
                }

                is StoreReadResponse.Error.Message -> {
                    Log.e(TAG, "Error during data load: ${response.message}")
                    TODO("Show error")
                }

                is StoreReadResponse.NoNewData -> {
                    // no-op
                }
            }
        }
    }

    Crossfade(targetState = isLoading, label = "Data Loader") {
        if (it) {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                loadingView()
            }
        } else {
            content(data)
        }
    }
}
