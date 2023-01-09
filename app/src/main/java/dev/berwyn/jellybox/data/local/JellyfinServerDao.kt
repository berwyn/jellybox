package dev.berwyn.jellybox.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JellyfinServerDao {
    @Query("SELECT * FROM jellyfinserver")
    fun getAll(): Flow<List<JellyfinServer>>

    @Query("SELECT * FROM jellyfinserver WHERE is_default = 1 LIMIT 1")
    suspend fun getDefault(): JellyfinServer?

    @Query("SELECT * FROM jellyfinserver WHERE id = :id")
    suspend fun findById(id: Long): JellyfinServer

    @Query("SELECT * FROM jellyfinserver WHERE id = :id")
    suspend fun findById(id: Int): JellyfinServer

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun storeServer(server: JellyfinServer): Long

    @Delete
    suspend fun deleteServer(server: JellyfinServer)
}
