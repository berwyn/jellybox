package dev.berwyn.jellybox.ui.screens

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val screensModule = module {
    factoryOf(::HomeScreenModel)
}
