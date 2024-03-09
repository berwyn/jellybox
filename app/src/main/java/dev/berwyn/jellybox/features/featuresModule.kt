package dev.berwyn.jellybox.features

import dev.berwyn.jellybox.features.playback.playbackModule
import org.koin.dsl.module

val featuresModule = module {
    includes(
        playbackModule
    )
}
