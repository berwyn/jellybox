package dev.berwyn.jellybox.ui.onboarding

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import dev.berwyn.jellybox.ui.components.Wizard
import org.koin.compose.koinInject

@Composable
fun OnboardingScreen(
    onSetupComplete: () -> Unit,
    viewModel: OnboardingScreenViewModel = koinInject(),
) {
    LaunchedEffect(viewModel) {
        viewModel.discoverLocalServers()
    }

    Scaffold { contentPadding ->
        Wizard(indicatorPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding())) {
            page("welcome") {
                OnboardingWelcomePage(
                    onNextClicked = { goToNextPage() },
                    modifier = Modifier.padding(contentPadding),
                )
            }

            page("server") {
                OnboardingServerPage(
                    loading = viewModel.loading,
                    isServerValid = viewModel.serverAddress.isNotEmpty(),
                    localServers = viewModel.localServers,
                    validateServer = viewModel::checkServerAddress,
                    onNextClicked = { goToNextPage() },
                    onBackClicked = { goToPreviousPage() },
                    modifier = Modifier.padding(contentPadding),
                )
            }

            page("credentials") {
                OnboardingCredentialsPage(
                    isLoading = viewModel.loading,
                    retainCredentials = viewModel.retainCredentials,
                    onRetainCredentialsChanged = { viewModel.retainCredentials = it },
                    onLoginClicked = { username, password ->
                        viewModel.login(username, password, onSuccess = { goToNextPage() })
                    },
                    modifier = Modifier.padding(contentPadding),
                )
            }

            page("complete") {
                OnboardingCompletePage(
                    onExitClicked = onSetupComplete,
                    modifier = Modifier.padding(contentPadding),
                )
            }
        }
    }
}
