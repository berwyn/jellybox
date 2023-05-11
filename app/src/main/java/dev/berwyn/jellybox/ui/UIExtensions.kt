package dev.berwyn.jellybox.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior

@OptIn(ExperimentalMaterial3Api::class)
val TopAppBarScrollBehavior.isCollapsed: Boolean
    get() = state.collapsedFraction == 1.0f
