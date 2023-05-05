package dev.berwyn.jellybox.data.local

import androidx.room.Entity
import java.util.UUID

@Entity(
    primaryKeys = ["collectionId", "mediaItemId"],
)
data class CollectionMediaItem(
    val collectionId: UUID,
    val mediaItemId: UUID,
)
