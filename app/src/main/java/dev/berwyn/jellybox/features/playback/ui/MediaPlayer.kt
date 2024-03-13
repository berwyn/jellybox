package dev.berwyn.jellybox.features.playback.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import com.skydoves.orbital.Orbital
import com.skydoves.orbital.rememberContentWithOrbitalScope
import dev.berwyn.jellybox.ui.previews.DynamicColourPreviews
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.theme.JellyboxTheme

@Composable
private fun MediaPlayer(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean = false,
    isLoading: Boolean = false,
) {
    val strings = LocalStrings.current

    val artwork = rememberContentWithOrbitalScope {
        val width = if (isCollapsed) 48.dp else 256.dp
        val height = if (isCollapsed) 48.dp else 256.dp

        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier
                    .width(width)
                    .height(height)
                    .aspectRatio(1f),
                color = MaterialTheme.colorScheme.tertiaryContainer,
            ) {
                // TODO replace with cover art
            }

            if (isLoading) {
                CircularProgressIndicator()
            }
        }
    }

    val controls = rememberContentWithOrbitalScope {
        val height = if (isCollapsed) 48.dp else 96.dp

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            IconButton(modifier = Modifier.height(height), onClick = { /*TODO*/ }) {
                Icon(Icons.Default.SkipPrevious, strings.playback.previous)
            }

            IconButton(modifier = Modifier.height(height), onClick = { /*TODO*/ }) {
                Icon(Icons.Default.PlayArrow, strings.playback.play)
            }

            IconButton(modifier = Modifier.height(height), onClick = { /*TODO*/ }) {
                Icon(Icons.Default.SkipNext, strings.playback.next)
            }
        }
    }

    val backgroundColour = when (isCollapsed) {
        true -> MaterialTheme.colorScheme.surface
        false -> MaterialTheme.colorScheme.secondaryContainer
    }

    Orbital(modifier = modifier.background(backgroundColour)) {
        if (isCollapsed) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
                artwork()
                controls()
            }
        } else {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)) {
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(), contentAlignment = Alignment.Center) {
                    artwork()
                }

                controls()
            }
        }
    }
}

@Composable
@ThemePreviews
@DynamicColourPreviews
private fun CollapsedPreview() {
    JellyboxTheme {
        MediaPlayer(
            isCollapsed = true,
            isLoading = true
        )
    }
}

@Composable
@ThemePreviews
@DynamicColourPreviews
private fun ExpandedPreviews() {
    JellyboxTheme {
        MediaPlayer(
            isCollapsed = false,
            isLoading = true
        )
    }
}
