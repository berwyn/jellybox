package dev.berwyn.jellybox

import dev.berwyn.jellybox.data.dataModule
import dev.berwyn.jellybox.data.jellyfinModule
import dev.berwyn.jellybox.data.local.databaseModule
import dev.berwyn.jellybox.domain.domainModule
import dev.berwyn.jellybox.features.featuresModule
import dev.berwyn.jellybox.security.securityModule
import dev.berwyn.jellybox.ui.uiModule
import org.koin.dsl.module

val jellyboxModule = module {
    includes(
        jellyfinModule,
        securityModule,
        databaseModule,
        dataModule,
        uiModule,
        domainModule,
        featuresModule,
    )
}
