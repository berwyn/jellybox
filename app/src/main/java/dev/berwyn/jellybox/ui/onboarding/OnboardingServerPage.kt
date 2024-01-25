package dev.berwyn.jellybox.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.jellyfin.sdk.model.api.ServerDiscoveryInfo

@Composable
@OptIn(FlowPreview::class)
fun OnboardingServerPage(
    loading: Boolean,
    isServerValid: Boolean,
    localServers: List<ServerDiscoveryInfo>,
    validateServer: (String) -> Unit,
    onNextClicked: () -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(validateServer) {
        snapshotFlow { text }
            .distinctUntilChanged()
            .debounce(250L)
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(1_500),
                initialValue = "",
            )
            .collect(validateServer)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Server address") },
            trailingIcon = {
                if (loading) {
                    CircularProgressIndicator()
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            mainAxisAlignment = FlowMainAxisAlignment.Start,
            mainAxisSpacing = 4.dp
        ) {
            localServers.forEach { server ->
                SuggestionChip(
                    onClick = { /*TODO*/ },
                    label = { Text(server.name) }
                )
            }
        }

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            FilledTonalButton(onClick = onBackClicked) {
                Text("Back")
            }

            Button(onClick = onNextClicked, enabled = isServerValid) {
                Text("Next")
            }
        }
    }
}

@Composable
@ThemePreviews
fun OnboardingServerPagePreview() {
    JellyboxTheme {
        OnboardingServerPage(
            loading = false,
            isServerValid = false,
            localServers = persistentListOf(
                ServerDiscoveryInfo(name = "Foo", address = "https://foo", id = "foo", endpointAddress = null),
                ServerDiscoveryInfo(name = "Bar", address = "https://bar", id = "bar", endpointAddress = null),
            ),
            validateServer = { },
            onNextClicked = { },
            onBackClicked = { },
        )
    }
}
