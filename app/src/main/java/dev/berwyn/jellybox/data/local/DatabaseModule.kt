package dev.berwyn.jellybox.data.local

import androidx.room.Room
import dev.berwyn.jellybox.data.DatabaseJellyfinServerRepository
import dev.berwyn.jellybox.data.JellyfinServerRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            JellyboxDatabase::class.java,
            "jellybox"
        )
            .addMigrations(MIGRATION_2_3)
            .build()
    }

    singleOf(::DatabaseJellyfinServerRepository) {
        bind<JellyfinServerRepository>()
    }
}
