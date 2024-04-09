package dev.berwyn.jellybox.data.store

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.data.local.Album
import dev.berwyn.jellybox.data.local.Jellybox
import dev.berwyn.jellybox.data.local.Server
import dev.berwyn.jellybox.data.local.Track
import dev.berwyn.jellybox.domain.CreateClientUseCase
import kotlinx.coroutines.Dispatchers
import org.jellyfin.sdk.api.client.extensions.itemsApi
import org.jellyfin.sdk.api.client.extensions.userLibraryApi
import org.jellyfin.sdk.model.api.BaseItemKind
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.FetcherResult
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder
import java.util.UUID

enum class Stores {
    LatestAlbums,
    Album,
    AlbumTrack,
}

typealias LatestAlbumStore = Store<Server, List<Album>>
typealias AlbumStore = Store<UUID, Album>
typealias AlbumTrackStore = Store<Album, List<Track>>

val storeModule = module {
    single<LatestAlbumStore>(named(Stores.LatestAlbums)) {
        val jellybox = get<Jellybox>()
        val createClient = get<CreateClientUseCase>()

        StoreBuilder.from<Server, List<Album>, List<Album>>(
            fetcher = Fetcher.ofResult { server: Server ->
                val client = createClient(server, validateSession = true)

                runCatching {
                    val albums by client.userLibraryApi
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
                }
                    .map { FetcherResult.Data(it) }
                    .getOrElse { FetcherResult.Error.Exception(it) }

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

    single<AlbumStore>(named(Stores.Album)) {
        val jellybox = get<Jellybox>()
        val applicationState = get<ApplicationState>()

        StoreBuilder.from<UUID, Album, Album>(
            fetcher = Fetcher.ofResult { id: UUID ->
                val client = applicationState.currentClient
                    ?: return@ofResult FetcherResult.Error.Message("Client not configured")

                runCatching {
                    val response by client.itemsApi.getItems(
                        userId = client.userId,
                        ids = listOf(id),
                        includeItemTypes = listOf(BaseItemKind.MUSIC_ALBUM)
                    )

                    val dto = response.items
                        .orEmpty()
                        .first()

                    Album(
                        id = dto.id,
                        name = dto.name!!,
                        // No idea what ticks are, but the official client uses this to convert to millis
                        duration = dto.runTimeTicks!!.div(10000)
                    )
                }
                    .map { FetcherResult.Data(it) }
                    .getOrElse { FetcherResult.Error.Exception(it) }
            },
            sourceOfTruth = SourceOfTruth.of(
                reader = { id ->
                    jellybox.albumQueries.getById(id)
                        .asFlow()
                        .mapToOneOrNull(Dispatchers.IO)
                },
                writer = { _, album ->
                    jellybox.albumQueries.upsert(album)
                }
            )
        ).build()
    }

    single<AlbumTrackStore>(named(Stores.AlbumTrack)) {
        val jellybox = get<Jellybox>()
        val applicationState = get<ApplicationState>()

        StoreBuilder.from<Album, List<Track>, List<Track>>(
            fetcher = Fetcher.ofResult { album: Album ->
                val client = applicationState.currentClient
                    ?: return@ofResult FetcherResult.Error.Message("Client not configured")

                runCatching {
                    val result by client.itemsApi.getItems(
                        userId = client.userId,
                        parentId = album.id,
                    )

                    result.items.orEmpty().map { dto ->
                        Track(
                            id = dto.id,
                            name = dto.name!!,
                            // No idea what ticks are, but the official client uses this to convert to millis
                            duration = dto.runTimeTicks!!.div(10000),
                            albumId = album.id,
                        )
                    }
                }
                    .map { FetcherResult.Data(it) }
                    .getOrElse { FetcherResult.Error.Exception(it) }
            },
            sourceOfTruth = SourceOfTruth.of(
                reader = { album ->
                    jellybox.trackQueries.getTracksForAlbum(album.id)
                        .asFlow()
                        .mapToList(Dispatchers.IO)
                },
                writer = { _, tracks ->
                    tracks.forEach {
                        jellybox.trackQueries.upsert(it)
                    }
                }
            )
        ).build()
    }
}
