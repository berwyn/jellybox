package dev.berwyn.jellybox.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dev.berwyn.jellybox.ui.Wizard

@Composable
fun OnboardingScreen(
    viewModel: OnboardingScreenViewModel = hiltViewModel()
) {
    Wizard {
        page("welcome") {
            OnboardingWelcomePage(onNextClicked = { goToNextPage() })
        }

        page("server") {
            OnboardingServerPage(
                viewModel = viewModel,
                onNextClicked = { goToNextPage() },
                onBackClicked = { goToPreviousPage() },
            )
        }
    }
}