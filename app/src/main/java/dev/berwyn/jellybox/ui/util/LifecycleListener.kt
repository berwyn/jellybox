package dev.berwyn.jellybox.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun LifecycleObserver(
    vararg events: Lifecycle.Event,
    onEvent: LifecycleOwner.(Lifecycle.Event) -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { owner, event ->
            if (event in events) {
                owner.onEvent(event)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
