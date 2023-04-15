package dev.berwyn.jellybox.domain

import dev.berwyn.jellybox.security.SecurePrefs
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetActiveServerUseCase)
    factoryOf(::SelectActiveServerUseCase)

    factory {
        RetrieveServerCredentialUseCase(get(SecurePrefs))
    }


    factory {
        StoreServerUseCase(
            get(SecurePrefs),
            get(),
            get(),
        )
    }
}
