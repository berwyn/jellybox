package dev.berwyn.jellybox.data.store

import android.util.Log
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import dev.berwyn.jellybox.data.ApplicationState
import dev.berwyn.jellybox.data.local.Album
import dev.berwyn.jellybox.data.local.Jellybox
import dev.berwyn.jellybox.data.local.Server
import dev.berwyn.jellybox.data.local.Track
import dev.berwyn.jellybox.domain.CreateClientUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import org.jellyfin.sdk.api.client.extensions.itemsApi
import org.jellyfin.sdk.api.client.extensions.libraryApi
import org.jellyfin.sdk.api.client.extensions.userLibraryApi
import org.jellyfin.sdk.model.api.BaseItem
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.FetcherResult
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

enum class Stores {
    LatestAlbums,
    Album,
    Albums,
    AlbumTrack,
}

typealias LatestAlbumStore = Store<Server, List<Album>>
typealias AlbumStore = Store<UUID, Album>
typealias AlbumsStore = Store<Server, ImmutableList<Album>>
typealias AlbumTrackStore = Store<Album, List<Track>>

private fun BaseItemDto.asAlbum(timestamp: Instant): Album = Album(
    id = id,
    name = name!!,
    // No idea what 'ticks' are but this is what the official client does
    duration = runTimeTicks!!.div(10_000),
    createdAt = dateCreated?.toInstant(ZoneOffset.UTC) ?: timestamp,
    updatedAt = timestamp,
)

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

                    val timestamp = Instant.now()

                    albums.map { it.asAlbum(timestamp) }
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
                        .map {
                            it.sortedByDescending(Album::createdAt).take(5)
                        }
                },
                writer = { server, albums ->
                    albums.forEach { album ->
                        jellybox.albumQueries.upsertWithServer(
                            id = album.id,
                            name = album.name,
                            duration = album.duration,
                            createdAt = album.createdAt,
                            updatedAt = album.updatedAt,
                            serverId = server.id,
                        )
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

                    val timestamp = Instant.now()

                    dto.asAlbum(timestamp)
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

    single<AlbumsStore>(named(Stores.Albums)) {
        val jellybox = get<Jellybox>()
        val createClient = get<CreateClientUseCase>()

        StoreBuilder.from<Server, ImmutableList<Album>, ImmutableList<Album>>(
            fetcher = Fetcher.ofResult {  server: Server ->
                val client = createClient(server, validateSession = true)

                runCatching {
                    val result by client.itemsApi.getItems(
                        userId = client.userId,
                        includeItemTypes = listOf(BaseItemKind.MUSIC_ALBUM),
                        recursive = true,
                    )

                    Log.d("Albums Store", "Got ${result.totalRecordCount} items from server")

                    val timestamp = Instant.now()

                    result.items?.map { it.asAlbum(timestamp) }
                        .orEmpty()
                        .toImmutableList()
                }

                    .map { FetcherResult.Data(it) }
                    .getOrElse { FetcherResult.Error.Exception(it) }
            },
            sourceOfTruth = SourceOfTruth.of(
                reader = { server ->
                    jellybox.albumQueries.getAllForServer(server.id)
                        .asFlow()
                        .mapToList(Dispatchers.IO)
                        .map { it.sortedByDescending(Album::createdAt).toImmutableList() }
                },
                writer = { server, albums ->
                    albums.forEach { album ->
                        jellybox.albumQueries.upsertWithServer(
                            id = album.id,
                            name = album.name,
                            duration = album.duration,
                            createdAt = album.createdAt,
                            updatedAt = album.updatedAt,
                            serverId = server.id
                        )
                    }
                }
            )
        ).build()
    }
}
