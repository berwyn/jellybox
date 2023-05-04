package dev.berwyn.jellybox.ui.locals

import androidx.activity.ComponentActivity
import androidx.compose.runtime.staticCompositionLocalOf

val LocalActivity = staticCompositionLocalOf<ComponentActivity> {
    error("LocalActivity is not set!")
}
