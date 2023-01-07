package dev.berwyn.jellybox.data

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.berwyn.jellybox.BuildConfig
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.android.androidDevice
import org.jellyfin.sdk.createJellyfin
import org.jellyfin.sdk.model.ClientInfo

@Module
@InstallIn(SingletonComponent::class)
object JellyfinModule {
    @Provides
    fun providesJellyfin(@ApplicationContext appContext: Context): Jellyfin = createJellyfin {
        clientInfo = ClientInfo(name = "Jellybox", version = BuildConfig.VERSION_NAME)
        deviceInfo = androidDevice(appContext)
        context = appContext
    }

    @Provides
    fun bindsJellyfinState(jellyfin: Jellyfin): JellyfinState = DefaultJellyfinState(jellyfin = jellyfin)
}