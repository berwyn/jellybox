package dev.berwyn.jellybox.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JellyfinServerDao {
    @Query("SELECT * FROM jellyfinserver")
    fun getAll(): Flow<List<JellyfinServer>>

    @Query("SELECT * FROM jellyfinserver WHERE is_default = 1")
    suspend fun getDefault(): JellyfinServer?

    @Query("SELECT * FROM jellyfinserver WHERE is_selected = 1")
    suspend fun getSelected(): JellyfinServer?

    @Query("SELECT * FROM jellyfinserver WHERE is_selected = 1")
    fun getSelectedFlow(): Flow<JellyfinServer?>

    @Query("SELECT * FROM jellyfinserver WHERE id = :id")
    suspend fun findById(id: Long): JellyfinServer

    @Query("SELECT * FROM jellyfinserver WHERE id = :id")
    suspend fun findById(id: Int): JellyfinServer

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun storeServer(server: JellyfinServer): Long

    @Update
    suspend fun updateServer(server: JellyfinServer)

    @Delete
    suspend fun deleteServer(server: JellyfinServer)
}
