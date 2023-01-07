package dev.berwyn.jellybox.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import dev.berwyn.jellybox.ui.previews.ThemePreview
import dev.berwyn.jellybox.ui.theme.JellyboxTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.jellyfin.sdk.createJellyfin

@Composable
@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
fun OnboardingServerPage(
    viewModel: OnboardingScreenViewModel,
    onNextClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    val inputValid by remember {
        derivedStateOf { text.isNotEmpty() && viewModel.serverAddress.isNotEmpty() }
    }

    LaunchedEffect(viewModel) {
        snapshotFlow { text }
            .distinctUntilChanged()
            .debounce(250L)
            .collect(viewModel::checkServerAddress)

        viewModel.discoverLocalServers()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Server address") },
            trailingIcon = {
                if (viewModel.loading) {
                    CircularProgressIndicator()
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        FlowRow() {
            viewModel.localServers.forEach { server ->
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

            Button(onClick = onNextClicked, enabled = inputValid) {
                Text("Next")
            }
        }
    }
}

@Composable
@ThemePreview
fun PreviewOnboardingServerList() {
    JellyboxTheme {
        OnboardingServerPage(
            // TODO(berwyn): abstract away jellyfin becuse previews are broken and it sucks
            OnboardingScreenViewModel(createJellyfin { context = LocalContext.current }),
            onNextClicked = { },
            onBackClicked = { },
        )
    }
}