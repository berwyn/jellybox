package dev.berwyn.jellybox.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.berwyn.jellybox.ui.navigation.NavigationType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationState {
    var navigationType: NavigationType by mutableStateOf(NavigationType.Bar)
    var navigationHidden: Boolean by mutableStateOf(false)
}
