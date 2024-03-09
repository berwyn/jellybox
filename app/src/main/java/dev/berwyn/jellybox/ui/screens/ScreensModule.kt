package dev.berwyn.jellybox.ui.screens

import dev.berwyn.jellybox.ui.screens.home.HomeScreenModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val screensModule = module {
    factoryOf(::HomeScreenModel)
}
