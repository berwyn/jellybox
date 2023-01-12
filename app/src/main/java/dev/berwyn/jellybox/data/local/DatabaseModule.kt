package dev.berwyn.jellybox.data.local

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.berwyn.jellybox.data.DatabaseJellyfinServerRepository
import dev.berwyn.jellybox.data.JellyfinServerRepository

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun providesJellyboxDatabase(
        @ApplicationContext context: Context
    ): JellyboxDatabase = Room.databaseBuilder(
        context,
        JellyboxDatabase::class.java,
        "jellybox"
    ).build()

    @Provides
    fun bindsServerRepository(impl: DatabaseJellyfinServerRepository): JellyfinServerRepository = impl
}
