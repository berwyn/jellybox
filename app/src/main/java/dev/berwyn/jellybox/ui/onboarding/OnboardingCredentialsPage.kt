package dev.berwyn.jellybox.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import dev.berwyn.jellybox.ui.previews.ThemePreview
import dev.berwyn.jellybox.ui.theme.JellyboxTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun OnboardingCredentialsPage(
    isLoading: Boolean,
    retainCredentials: Boolean,
    onRetainCredentialsChanged: (Boolean) -> Unit,
    onLoginClicked: (username: String, password: String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp),
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
            ),
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = retainCredentials,
                    onValueChange = onRetainCredentialsChanged,
                    role = Role.Checkbox,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Checkbox(checked = retainCredentials, onCheckedChange = null)
            Text(
                text = "Remember credentials?",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onLoginClicked(username, password) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Login")
            }
        }
    }
}

@Composable
@ThemePreview
fun OnboardingCredentialsPagePreview() {
    JellyboxTheme {
        OnboardingCredentialsPage(
            isLoading = false,
            retainCredentials = false,
            onRetainCredentialsChanged = { },
            onLoginClicked = { _, _ -> },
        )
    }
}
