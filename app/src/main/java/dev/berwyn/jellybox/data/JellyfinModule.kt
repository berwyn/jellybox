package dev.berwyn.jellybox.data

import dev.berwyn.jellybox.BuildConfig
import org.jellyfin.sdk.android.androidDevice
import org.jellyfin.sdk.createJellyfin
import org.jellyfin.sdk.model.ClientInfo
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val jellyfinModule = module {
    single {
        createJellyfin {
            clientInfo = ClientInfo(name = "Jellybox", version = BuildConfig.VERSION_NAME)
            deviceInfo = androidDevice(androidContext())
            context = androidContext()
        }
    }

    singleOf(::DatabaseJellyfinServerRepository)
}
