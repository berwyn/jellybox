package dev.berwyn.jellybox.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class MediaItem(
    @PrimaryKey
    val id: UUID,
    val name: String,
    val imageAspectRatio: Double,
    val type: MediaItemType,
    val parentId: UUID,
)

