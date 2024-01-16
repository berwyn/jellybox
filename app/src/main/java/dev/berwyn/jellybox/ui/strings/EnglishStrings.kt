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
)
