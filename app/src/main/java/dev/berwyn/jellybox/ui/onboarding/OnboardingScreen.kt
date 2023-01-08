package dev.berwyn.jellybox.ui.onboarding

import androidx.compose.runtime.*
import androidx.compose.runtime.internal.isLiveLiteralsEnabled
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import dev.berwyn.jellybox.ui.Wizard

@Composable
fun OnboardingScreen(
    onSetupComplete: () -> Unit,
    viewModel: OnboardingScreenViewModel = hiltViewModel(),
) {
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
