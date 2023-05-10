package dev.berwyn.jellybox.data

import androidx.room.withTransaction
import dev.berwyn.jellybox.data.local.CollectionMediaItem
import dev.berwyn.jellybox.data.local.JellyboxDatabase
import dev.berwyn.jellybox.data.local.MediaCollection
import dev.berwyn.jellybox.data.local.MediaCollectionWithItems
import dev.berwyn.jellybox.data.local.MediaItem
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jellyfin.sdk.api.client.extensions.itemsApi
import org.jellyfin.sdk.api.client.extensions.userLibraryApi
import org.jellyfin.sdk.api.client.extensions.userViewsApi
import org.jellyfin.sdk.model.api.ItemFields
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.FetcherResult
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.StoreBuilder
import java.util.UUID

val MediaCollectionStore = named("media collection")
val MediaItemStore = named("media item")
val LatestMediaItemsStore = named("latest media items")
val CollectionMediaItemStore = named("collection media items")

val dataModule = module {
    singleOf(::ApplicationState)

    single(MediaCollectionStore) {
        val state: ApplicationState = get()
        val db: JellyboxDatabase = get()

        StoreBuilder.from<UUID, List<MediaCollection>, List<MediaCollection>>(
            fetcher = Fetcher.ofResult {
                val client = state.jellyfinClient

                client ?: return@ofResult FetcherResult.Error.Message("Client not configured")

                try {
                    val dtos by client.userViewsApi.getUserViews(userId = it)

                    val views = dtos.items
                        .orEmpty()
                        .map { dto ->
                            MediaCollection(id = dto.id, name = dto.name ?: dto.collectionType ?: "Unnamed")
                        }

                    FetcherResult.Data(views)
                } catch (error: Error) {
                    FetcherResult.Error.Exception(error)
                }
            },
            sourceOfTruth = SourceOfTruth.of(
                reader = { db.mediaCollectionDao().getMediaCollections() },
                writer = { _, item -> db.mediaCollectionDao().storeMediaCollection(item) },
            )
        ).build()
    }

    single(MediaItemStore) {
        val state: ApplicationState = get()
        val db: JellyboxDatabase = get()

        StoreBuilder.from<UUID, MediaItem, MediaItem>(
            fetcher = Fetcher.ofResult {
                val client = state.jellyfinClient

                client ?: return@ofResult FetcherResult.Error.Message("Client not configured")

                val dto by client.userLibraryApi.getItem(itemId = it)
                val item = dto.asMediaItem().getOrElse {
                    return@ofResult FetcherResult.Error.Exception(it)
                }

                FetcherResult.Data(item)
            },
            sourceOfTruth = SourceOfTruth.of(
                nonFlowReader = db.mediaItemDao()::findById,
                writer = { _, item -> db.mediaItemDao().storeMediaItems(item) },
                delete = db.mediaItemDao()::deleteMediaItem,
                deleteAll = db.mediaItemDao()::deleteAllMediaItems,
            )
        ).build()
    }

    single(LatestMediaItemsStore) {
        val state: ApplicationState = get()

        StoreBuilder.from<UUID, List<MediaItem>, List<MediaItem>>(
            fetcher = Fetcher.ofResult { id: UUID ->
                val client = state.jellyfinClient

                client ?: return@ofResult FetcherResult.Error.Message("Client not configured")

                val collection by client.userLibraryApi.getLatestMedia(
                    parentId = id,
                    enableImages = true,
                    fields = persistentListOf(
                        ItemFields.PARENT_ID,
                        ItemFields.PRIMARY_IMAGE_ASPECT_RATIO
                    ),
                )

                val items = collection.map { it.asMediaItem().getOrThrow() }.toImmutableList()

                FetcherResult.Data(items)
            },
        )
            .build()
    }

    single(CollectionMediaItemStore) {
        val state: ApplicationState = get()
        val db: JellyboxDatabase = get()

        StoreBuilder.from<UUID, MediaCollectionWithItems, MediaCollectionWithItems>(
            fetcher = Fetcher.ofResult { collectionId ->
                val client = state.jellyfinClient

                client ?: return@ofResult FetcherResult.Error.Message("Client not configured")

                val collection = db.mediaCollectionDao().getMediaCollection(collectionId)

                collection ?: return@ofResult FetcherResult.Error.Message("Unknown collection requested!")

                try {
                    val response by client.itemsApi.getItemsByUserId(
                        parentId = collectionId,
                        enableImages = true,
                        fields = persistentListOf(
                            ItemFields.PARENT_ID,
                            ItemFields.PRIMARY_IMAGE_ASPECT_RATIO,
                        )
                    )

                    FetcherResult.Data(
                        MediaCollectionWithItems(
                            collection,
                            response.items.orEmpty().map { it.asMediaItem().getOrThrow() }
                        )
                    )
                } catch (error: Exception) {
                    FetcherResult.Error.Exception(error)
                }
            },
            sourceOfTruth = SourceOfTruth.of(
                reader = {
                    db.mediaCollectionDao().getCollectionWithItems(it)
                },
                writer = { _, collection ->
                    db.withTransaction {
                        db.mediaCollectionDao().storeMediaCollection(collection.collection)
                        db.mediaItemDao().storeMediaItems(collection.items)

                        db.mediaCollectionDao().storeCollectionMediaItems(
                            collection.items.map { CollectionMediaItem(collection.collection.id, it.id) }
                        )
                    }
                }
            )
        ).build()
    }
}
