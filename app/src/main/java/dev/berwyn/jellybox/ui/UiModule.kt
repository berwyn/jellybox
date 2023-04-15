package dev.berwyn.jellybox.ui

import dev.berwyn.jellybox.ui.onboarding.OnboardingScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::JellyboxAppViewModel)
    viewModelOf(::HomeScreenViewModel)

    viewModelOf(::OnboardingScreenViewModel)
}
