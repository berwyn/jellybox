package dev.berwyn.jellybox.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DatabaseConverters {
    @TypeConverter
    fun stringsToJson(value: List<String>?): String? = value?.let {
        Json.encodeToString(value)
    }

    @TypeConverter
    fun jsonToStrings(value: String?): List<String>? = value?.let {
        Json.decodeFromString(it)
    }
}
