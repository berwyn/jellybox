package dev.berwyn.jellybox.data.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

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
}
