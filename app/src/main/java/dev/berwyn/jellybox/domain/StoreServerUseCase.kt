package dev.berwyn.jellybox.domain

import android.content.SharedPreferences
import dev.berwyn.jellybox.data.local.JellyboxDatabase
import dev.berwyn.jellybox.data.local.JellyfinServer
import dev.berwyn.jellybox.security.SecurePrefs
import javax.inject.Inject

class StoreServerUseCase @Inject constructor(
    @SecurePrefs private val securePrefs: SharedPreferences,
    private val jellyboxDatabase: JellyboxDatabase,
) {
    suspend operator fun invoke(
        name: String,
        uri: String,
        authToken: String,
        credentialRetention: StoreServerCredentialRetention = StoreServerCredentialRetention.NONE
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
                .storeServer(JellyfinServer.newServer(name, uri))
                .let { id -> jellyboxDatabase.serverDao().findById(id) }
        }
    }
}

enum class StoreServerCredentialRetention {
    NONE,
    RETAIN,
}

class CredentialStorageFailedException : Exception("Credential storage failed") {}
