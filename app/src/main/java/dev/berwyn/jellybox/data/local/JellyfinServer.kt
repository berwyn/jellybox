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
    var isDefault: Boolean = false,

    @ColumnInfo(name = "is_selected")
    var isSelected: Boolean = false,
) {
    companion object {
        fun create(name: String, uri: String) = JellyfinServer(
            id = 0,
            name = name,
            uri = uri,
        )
    }
}

