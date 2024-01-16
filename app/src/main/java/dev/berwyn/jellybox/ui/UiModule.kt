package dev.berwyn.jellybox.ui

import dev.berwyn.jellybox.data.MediaItemStore
import dev.berwyn.jellybox.ui.media.MediaItemScreenViewModel
import dev.berwyn.jellybox.ui.onboarding.OnboardingScreenViewModel
import dev.berwyn.jellybox.ui.screens.screensModule
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    includes(screensModule)

    viewModelOf(::JellyboxAppViewModel)
    viewModelOf(::HomeScreenViewModel)

    viewModelOf(::OnboardingScreenViewModel)

    viewModel {
        MediaItemScreenViewModel(
            savedStateHandle = get(),
            applicationState = get(),
            itemStore = get(MediaItemStore),
        )
    }
}
