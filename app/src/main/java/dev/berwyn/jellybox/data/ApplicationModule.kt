package dev.berwyn.jellybox.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.berwyn.jellybox.data.local.JellyboxDatabase

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    fun provideApplicationState(database: JellyboxDatabase) = ApplicationState(
        database = database,
    )
}
