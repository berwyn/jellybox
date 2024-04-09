package dev.berwyn.jellybox.ui.strings

data class Strings(
    val serverLabel: (count: Int) -> String,
    val addNewServer: String,

    val tabs: TabsStrings,

    val homeScreen: HomeScreenStrings,
    val playback: PlaybackStrings,
)

data class TabsStrings(
    val home: String,
    val albums: String,
)

data class HomeScreenStrings(
    val latestAlbums: String
)

data class PlaybackStrings (
    val previous: String,
    val play: String,
    val pause: String,
    val next: String,
)
