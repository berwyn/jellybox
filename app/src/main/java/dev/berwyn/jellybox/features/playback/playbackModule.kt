package dev.berwyn.jellybox.features.playback

import dev.berwyn.jellybox.features.playback.ui.MediaPlayerState
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playbackModule = module {
    viewModel {
        MediaPlayerState(get(), get())
    }
}
