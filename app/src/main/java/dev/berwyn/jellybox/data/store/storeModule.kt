package dev.berwyn.jellybox.data.store

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.berwyn.jellybox.data.local.Album
import dev.berwyn.jellybox.data.local.Jellybox
import dev.berwyn.jellybox.data.local.Server
import dev.berwyn.jellybox.domain.CreateClientUseCase
import dev.berwyn.jellybox.domain.RetrieveServerCredentialUseCase
import kotlinx.coroutines.Dispatchers
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.extensions.itemsApi
import org.jellyfin.sdk.api.client.extensions.userLibraryApi
import org.jellyfin.sdk.model.api.BaseItemKind
import org.koin.dsl.module
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

typealias LatestAlbumStore = Store<Server, List<Album>>

val storeModule = module {
    single<LatestAlbumStore> {
        val jellybox = get<Jellybox>()

        val createClient = get<CreateClientUseCase>()

        StoreBuilder.from<Server, List<Album>, List<Album>>(
            fetcher = Fetcher.of { server: Server ->
                val albums by createClient(server, validateSession = true)
                    .userLibraryApi
                    .getLatestMedia(
                        includeItemTypes = listOf(BaseItemKind.MUSIC_ALBUM),
                        limit = 5
                    )

                albums.map {
                    Album(
                        id = it.id,
                        name = it.name!!,
                        duration = it.runTimeTicks!!.div(600_000_000),
                    )
                }
            },
            sourceOfTruth = SourceOfTruth.of(
                reader = { server ->
                    jellybox.albumQueries
                        .getByServer(server.url)
                        .asFlow()
                        .mapToList(Dispatchers.IO)
                },
                writer = { server, albums ->
                    albums.forEach { album ->
                        jellybox.albumQueries.upsertWithServer(album.id, album.name, album.duration, server.id)
                    }
                },
                // TODO Support delete
                delete = null,
                deleteAll = null,
            )
        ).build()
    }
}
