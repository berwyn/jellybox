package dev.berwyn.jellybox.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.UUID

@Entity
data class MediaCollection(
    @PrimaryKey
    val id: UUID,
    val name: String,
)

data class MediaCollectionWithItems(
    @Embedded
    val collection: MediaCollection,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            CollectionMediaItem::class,
            parentColumn = "collectionId",
            entityColumn = "mediaItemId",
        ),
    )
    val items: List<MediaItem>,
)
