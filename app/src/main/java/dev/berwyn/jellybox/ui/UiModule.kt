package dev.berwyn.jellybox.ui

import dev.berwyn.jellybox.ui.onboarding.OnboardingScreenViewModel
import dev.berwyn.jellybox.ui.screens.screensModule
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    includes(screensModule)

    viewModelOf(::OnboardingScreenViewModel)
}
