package dev.berwyn.jellybox.data

import dev.berwyn.jellybox.data.store.storeModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    includes(storeModule)
    
    singleOf(::ApplicationState)

    single {
        DatabaseJellyfinServerRepository(get())
    } bind JellyfinServerRepository::class
}
