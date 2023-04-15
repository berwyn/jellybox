package dev.berwyn.jellybox.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dev.berwyn.jellybox.data.local.JellyfinServer
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import kotlinx.collections.immutable.persistentListOf

@Composable
fun ServerSelectionMenu(
    servers: List<JellyfinServer>,
    modifier: Modifier = Modifier,
    onServerSelected: (JellyfinServer) -> Unit
) {
    var isOpen by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { isOpen = true }) {
            Icon(Icons.Default.Menu, contentDescription = "Select server")
        }

        DropdownMenu(expanded = isOpen, onDismissRequest = { isOpen = false }) {
            servers.forEach { server ->
                DropdownMenuItem(
                    text = { Text(server.name) },
                    onClick = {
                        onServerSelected(server)
                        isOpen = false
                    },
                )
            }
        }
    }
}

@Composable
@ThemePreviews
fun ServerSelectionMenuPreview() {
    ServerSelectionMenu(
        servers = persistentListOf(
            JellyfinServer.create("Test", "https://test.server/"),
        ),
        onServerSelected = { }
    )
}
