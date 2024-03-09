package dev.berwyn.jellybox.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dev.berwyn.jellybox.data.local.Server
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import io.ktor.http.Url
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID

@Composable
fun ServerSelectionMenu(
    servers: ImmutableList<Server>,
    modifier: Modifier = Modifier,
    onServerSelected: (Server) -> Unit
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
            Server(
                UUID.randomUUID(),
                "Test",
                Url("https://test.server/"),
                false,
                false,
            )
        ),
        onServerSelected = { }
    )
}
