package dev.berwyn.jellybox.domain

import android.content.SharedPreferences
import dev.berwyn.jellybox.data.local.JellyfinServer
import dev.berwyn.jellybox.security.SecurePrefs
import javax.inject.Inject

class RetrieveServerCredentialUseCase @Inject constructor(
    @SecurePrefs private val securePrefs: SharedPreferences
) {
    operator fun invoke(server: JellyfinServer) = securePrefs.getString("server/${server.uri}", null)
}
