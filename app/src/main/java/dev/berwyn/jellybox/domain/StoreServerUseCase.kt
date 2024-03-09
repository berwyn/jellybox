package dev.berwyn.jellybox.domain

import android.content.SharedPreferences
import dev.berwyn.jellybox.data.local.Jellybox
import dev.berwyn.jellybox.data.local.Server
import io.ktor.http.Url
import java.util.UUID

class StoreServerUseCase(
    private val securePrefs: SharedPreferences,
    private val jellyboxDatabase: Jellybox,
    private val selectActiveServer: SelectActiveServerUseCase,
) {
    suspend operator fun invoke(
        name: String,
        uri: String,
        authToken: String,
        credentialRetention: StoreServerCredentialRetention = StoreServerCredentialRetention.NONE,
        markActive: Boolean = true,
    ): Result<Server> {
        if (credentialRetention == StoreServerCredentialRetention.RETAIN) {
            val prefKey = "server/${uri}"

            val editor = securePrefs.edit().apply {
                putString(prefKey, authToken)
            }

            if (!editor.commit()) {
                return Result.failure(CredentialStorageFailedException())
            }
        }

        val shouldBeDefault = jellyboxDatabase.serverQueries
            .getCount()
            .executeAsOne() == 0L;

        val server = Server(UUID.randomUUID(), name, Url(uri), shouldBeDefault, false)

        return Result.runCatching {
            jellyboxDatabase.serverQueries.insert(server)

            if (markActive) {
                jellyboxDatabase.serverQueries.setSelected(server.id)
            }

            jellyboxDatabase.serverQueries.getServerById(server.id).executeAsOne()
        }
    }
}

enum class StoreServerCredentialRetention {
    NONE,
    RETAIN,
}

class CredentialStorageFailedException : Exception("Credential storage failed")
