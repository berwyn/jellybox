package dev.berwyn.jellybox.ui.util

import androidx.activity.ComponentActivity
import androidx.compose.runtime.staticCompositionLocalOf

val LocalActivity = staticCompositionLocalOf<ComponentActivity> {
    error("LocalActivity is not set!")
}
