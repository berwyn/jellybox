package dev.berwyn.jellybox.ui.strings

data class Strings(
    val serverLabel: (count: Int) -> String,
    val addNewServer: String,

    val playbackPrevious: String,
    val playbackPlay: String,
    val playbackPause: String,
    val playbackNext: String,
)
