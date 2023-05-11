package dev.berwyn.jellybox.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    version = 5,
    entities = [
        JellyfinServer::class,
        MediaCollection::class,
        MediaItem::class,
        CollectionMediaItem::class,
    ],
    autoMigrations = [
        // Add collection
        AutoMigration(from = 1, to = 2),
        // Add pivot table between collection & media item
        AutoMigration(from = 3, to = 4),
        // Add fields to `MediaItem`
        AutoMigration(from = 4, to = 5),
    ],
)
@TypeConverters(DatabaseConverters::class)
abstract class JellyboxDatabase : RoomDatabase() {
    abstract fun serverDao(): JellyfinServerDao

    abstract fun mediaItemDao(): MediaItemDao

    abstract fun mediaCollectionDao(): MediaCollectionDao
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 2 -> 3 adds required fields and has no novel data locally, therefore is safe to drop & repopulate from the network
        database.execSQL(
            """
            DROP TABLE `MediaItem`
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `MediaItem` (
                `id` BLOB NOT NULL,
                `name` TEXT NOT NULL,
                `imageAspectRatio` REAL NOT NULL,
                `type` TEXT NOT NULL,
                `parentId` BLOB NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `MediaCollection` (
                `id` BLOB NOT NULL,
                `name` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
    }
}
