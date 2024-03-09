package dev.berwyn.jellybox

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import app.cash.sqldelight.db.SqlSchema
import org.jellyfin.sdk.JellyfinOptions
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify

class JellyboxAppModuleCheck {
    @Test
    @OptIn(KoinExperimentalAPI::class)
    fun checkKoinModule() {
        jellyboxModule.verify(
            listOf(
                Boolean::class,
                JellyfinOptions.Builder::class,
                SupportSQLiteOpenHelper::class,
                SupportSQLiteOpenHelper.Factory::class,
                SupportSQLiteOpenHelper.Callback::class,
                SupportSQLiteDatabase::class,
                SqlSchema::class,
                Context::class,
            )
        )
    }
}
