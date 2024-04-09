package dev.berwyn.jellybox.ui.strings

import cafe.adriel.lyricist.LyricistStrings

@LyricistStrings(languageTag = Locales.EN, default = true)
val EnglishStrings = Strings(
    serverLabel = { count ->
        when (count) {
            1 -> "Server"
            else -> "Servers"
        }
    },
    addNewServer = "Add new server",

    tabs = TabsStrings(
        home = "Home",
        albums = "Albums",
    ),

    homeScreen = HomeScreenStrings(
        latestAlbums = "Latest Albums"
    ),

    playback = PlaybackStrings(
        next = "Next",
        play = "Play",
        pause = "Pause",
        previous = "Previous",
    ),
)
