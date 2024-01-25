package dev.berwyn.jellybox.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.berwyn.jellybox.ui.previews.ThemePreviews
import dev.berwyn.jellybox.ui.theme.JellyboxTheme

@Composable
fun OnboardingCompletePage(
    onExitClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Woohoo!", style = MaterialTheme.typography.headlineLarge)
        Button(onClick = onExitClicked, modifier = Modifier.fillMaxWidth()) {
            Text("Get started")
        }
    }
}

@Composable
@ThemePreviews
fun OnboardingCompletePagePreview() {
    JellyboxTheme {
        OnboardingCompletePage(onExitClicked = {})
    }
}
