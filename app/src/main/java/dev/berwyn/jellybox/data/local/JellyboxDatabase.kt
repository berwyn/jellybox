package dev.berwyn.jellybox.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [JellyfinServer::class],
    version = 1
)
abstract class JellyboxDatabase : RoomDatabase() {
    abstract fun serverDao(): JellyfinServerDao
}
