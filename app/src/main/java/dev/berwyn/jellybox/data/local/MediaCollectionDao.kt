package dev.berwyn.jellybox.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface MediaCollectionDao {
    @Query("SELECT * FROM MediaCollection")
    fun getMediaCollections(): Flow<List<MediaCollection>>

    @Query("SELECT * FROM MediaCollection WHERE id = :id")
    suspend fun getMediaCollection(id: UUID): MediaCollection?

    @Upsert
    suspend fun storeMediaCollection(vararg collections: MediaCollection)

    @Upsert
    suspend fun storeMediaCollection(collections: Collection<MediaCollection>)

    @Delete
    suspend fun deleteMediaCollection(vararg collections: MediaCollection)

    @Query("DELETE FROM MediaCollection WHERE id = :id")
    suspend fun deleteMediaCollection(id: UUID)

    @Transaction
    @Query("SELECT * FROM MediaCollection WHERE id = :id")
    fun getCollectionWithItems(id: UUID): Flow<MediaCollectionWithItems>

    @Upsert
    suspend fun storeCollectionMediaItems(vararg items: CollectionMediaItem)

    @Upsert
    suspend fun storeCollectionMediaItems(items: Collection<CollectionMediaItem>)
}
