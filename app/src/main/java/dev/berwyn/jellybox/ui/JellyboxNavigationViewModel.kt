package dev.berwyn.jellybox.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

enum class NavigationType {
    NONE,
    RAIL,
    BAR
}

@HiltViewModel
class JellyboxNavigationViewModel @Inject constructor() : ViewModel() {

}
