package dev.berwyn.jellybox.domain

import android.content.SharedPreferences
import dev.berwyn.jellybox.data.local.JellyboxDatabase
import dev.berwyn.jellybox.data.local.JellyfinServer

class StoreServerUseCase(
    private val securePrefs: SharedPreferences,
    private val jellyboxDatabase: JellyboxDatabase,
    private val selectActiveServer: SelectActiveServerUseCase,
) {
    suspend operator fun invoke(
        name: String,
        uri: String,
        authToken: String,
        credentialRetention: StoreServerCredentialRetention = StoreServerCredentialRetention.NONE,
        markActive: Boolean = true,
    ): Result<JellyfinServer> {
        if (credentialRetention == StoreServerCredentialRetention.RETAIN) {
            val prefKey = "server/${uri}"

            val didWrite = securePrefs
                .edit()
                .apply {
                    putString(prefKey, authToken)
                }
                .commit()

            if (!didWrite) {
                return Result.failure(CredentialStorageFailedException())
            }
        }

        return Result.runCatching {
            jellyboxDatabase
                .serverDao()
                .storeServer(JellyfinServer.create(name, uri))
                .let { id -> jellyboxDatabase.serverDao().findById(id) }
                .apply { if (markActive) selectActiveServer(this) }
        }
    }
}

enum class StoreServerCredentialRetention {
    NONE,
    RETAIN,
}

class CredentialStorageFailedException : Exception("Credential storage failed") {}
