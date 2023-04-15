package dev.berwyn.jellybox.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import dev.berwyn.jellybox.ui.Wizard
import org.koin.compose.koinInject

@Composable
fun OnboardingScreen(
    onSetupComplete: () -> Unit,
    viewModel: OnboardingScreenViewModel = koinInject(),
) {
    DisposableEffect(Unit) {
        viewModel.hideNavigation()

        onDispose {
            viewModel.restoreNavigation()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.discoverLocalServers()
    }

    Wizard {
        page("welcome") {
            OnboardingWelcomePage(onNextClicked = { goToNextPage() })
        }

        page("server") {
            OnboardingServerPage(
                loading = viewModel.loading,
                isServerValid = viewModel.serverAddress.isNotEmpty(),
                localServers = viewModel.localServers,
                validateServer = viewModel::checkServerAddress,
                onNextClicked = { goToNextPage() },
                onBackClicked = { goToPreviousPage() },
            )
        }

        page("credentials") {
            OnboardingCredentialsPage(
                isLoading = viewModel.loading,
                retainCredentials = viewModel.retainCredentials,
                onRetainCredentialsChanged = { viewModel.retainCredentials = it },
                onLoginClicked = { username, password ->
                    viewModel.login(username, password, onSuccess = { goToNextPage() })
                }
            )
        }

        page("complete") {
            OnboardingCompletePage(onExitClicked = onSetupComplete)
        }
    }
}
