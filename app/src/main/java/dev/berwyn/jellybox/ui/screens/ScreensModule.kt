package dev.berwyn.jellybox.ui.screens

import dev.berwyn.jellybox.data.store.Stores
import dev.berwyn.jellybox.ui.screens.album.AlbumListScreenModel
import dev.berwyn.jellybox.ui.screens.home.HomeScreenModel
import dev.berwyn.jellybox.ui.screens.album.AlbumScreenModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val screensModule = module {
    factoryOf(::HomeScreenModel)

    factory<AlbumScreenModel> {
        AlbumScreenModel(
            jellybox = get(),
            albumStore = get(named(Stores.Album)),
            trackStore = get(named(Stores.AlbumTrack))
        )
    }

    factory {
        AlbumListScreenModel(
            serverRepository = get(),
            albumsStore = get(named(Stores.Albums))
        )
    }
}
