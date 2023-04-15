package dev.berwyn.jellybox.data

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::ApplicationState)
}
