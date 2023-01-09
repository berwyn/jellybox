package dev.berwyn.jellybox.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class JellyfinServer(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "uri")
    val uri: String,
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean = false,
) {
    companion object {
        fun newServer(name: String, uri: String) = JellyfinServer(
            id = 0,
            name = name,
            uri = uri,
        )
    }
}

