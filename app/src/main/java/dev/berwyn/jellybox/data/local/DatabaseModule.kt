package dev.berwyn.jellybox.data.local

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.ktor.http.Url
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.UUID

private object UUIDAdapter : ColumnAdapter<UUID, String> {
    override fun decode(databaseValue: String): UUID = UUID.fromString(databaseValue)
    override fun encode(value: UUID): String = value.toString()
}

private object UrlAdapter : ColumnAdapter<Url, String> {
    override fun decode(databaseValue: String): Url = Url(databaseValue)
    override fun encode(value: Url): String = value.toString()

}

val databaseModule = module {
    single {
        AndroidSqliteDriver(
            Jellybox.Schema,
            androidContext(),
            "jellybox.db"
        )
    } bind SqlDriver::class

    single {
        Jellybox(
            driver = get(),
            AlbumAdapter = Album.Adapter(
                idAdapter = UUIDAdapter
            ),
            ServerAdapter = Server.Adapter(
                idAdapter = UUIDAdapter,
                urlAdapter = UrlAdapter,
            ),
            TrackAdapter = Track.Adapter(
                idAdapter = UUIDAdapter,
                albumIdAdapter = UUIDAdapter,
            ),
        )
    }
}
