package dev.berwyn.jellybox.ui.locals

import androidx.compose.runtime.staticCompositionLocalOf
import dev.berwyn.jellybox.data.ApplicationState

val LocalApplicationState = staticCompositionLocalOf<ApplicationState> {
    throw UnsupportedOperationException()
}
