package dev.berwyn.jellybox.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface MediaItemDao {
    @Query("SELECT * FROM mediaitem")
    fun getAll(): Flow<List<MediaItem>>

    @Query("SELECT * FROM mediaitem WHERE id = :id")
    suspend fun findById(id: UUID): MediaItem?

    @Upsert
    suspend fun storeMediaItems(vararg items: MediaItem)

    @Upsert
    suspend fun storeMediaItems(items: Collection<MediaItem>)

    @Delete
    suspend fun deleteMediaItems(vararg items: MediaItem)

    @Query("DELETE FROM mediaitem WHERE id = :id")
    suspend fun deleteMediaItem(id: UUID)

    @Query("DELETE FROM mediaitem")
    suspend fun deleteAllMediaItems()
}
