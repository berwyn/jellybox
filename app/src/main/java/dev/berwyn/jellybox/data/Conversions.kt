package dev.berwyn.jellybox.data

import dev.berwyn.jellybox.data.local.MediaItem
import dev.berwyn.jellybox.data.local.MediaItemType
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind


fun BaseItemDto.asMediaItem() =
    Result.runCatching {
        name ?: error("No name on media item!")
        parentId ?: error("No parent id on item!")
        primaryImageAspectRatio ?: error("No image aspect ratio on item!")

        MediaItem(
            id = id,
            name = name!!,
            parentId = parentId!!,
            imageAspectRatio = primaryImageAspectRatio!!,
            type = when (type) {
                BaseItemKind.MOVIE -> MediaItemType.MOVIE
                BaseItemKind.SERIES -> MediaItemType.SERIES
                BaseItemKind.EPISODE -> MediaItemType.EPISODE
                BaseItemKind.MUSIC_ALBUM -> MediaItemType.MUSIC_ALBUM
                else -> error("Invalid media item type!")
            },
        )
    }
