package dev.berwyn.jellybox.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import dev.berwyn.jellybox.data.local.Server
import dev.berwyn.jellybox.ui.previews.DynamicColourPreviews
import dev.berwyn.jellybox.ui.previews.PreviewHost
import dev.berwyn.jellybox.ui.previews.PreviewServerProvider
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreenAppbar(
    selectedServer: Server?,
    savedServers: ImmutableList<Server>,
    onServerSelected: (Server) -> Unit,
    onCreateNewServer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalStrings.current
    var isMenuOpen by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        title = {
            Box(modifier = Modifier.clickable { isMenuOpen = true }) {
                if (selectedServer != null) {
                    ServerChip(server = selectedServer)
                } else {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        text = strings.serverLabel(Int.MAX_VALUE),
                    )
                }

                DropdownMenu(
                    expanded = isMenuOpen,
                    onDismissRequest = { isMenuOpen = false },
                ) {
                    if (savedServers.isNotEmpty()) {
                        savedServers.forEach { server ->
                            ServerChip(
                                server = server,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clickable { onServerSelected(server) },
                                showActivityDot = server.id == selectedServer?.id,
                            )
                        }
                    }

                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text(strings.addNewServer) },
                        onClick = {
                            isMenuOpen = false
                            onCreateNewServer()
                        },
                    )
                }
            }
        },
    )
}

@Composable
private fun ServerChip(server: Server, modifier: Modifier = Modifier, showActivityDot: Boolean = false) {
    Row(
        modifier = modifier.sizeIn(maxHeight = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1.0f)
                .background(color = MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape),
        )

        Column {
            Text("Example Username", style = MaterialTheme.typography.titleLarge)

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(server.name, style = MaterialTheme.typography.titleSmall)

                if (showActivityDot) {
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .aspectRatio(1.0f)
                            .background(color = MaterialTheme.colorScheme.secondary, shape = CircleShape),
                    )
                }
            }
        }
    }
}

@Composable
@ThemePreviews
@DynamicColourPreviews
private fun HomeScreenAppbarPreview(
    @PreviewParameter(PreviewServerProvider::class) server: Server?,
) {
    PreviewHost {
        HomeScreenAppbar(
            selectedServer = server,
            savedServers = persistentListOf(),
            onServerSelected = {},
            onCreateNewServer = {},
        )
    }
}
