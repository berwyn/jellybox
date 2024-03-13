package dev.berwyn.jellybox

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import org.jellyfin.sdk.api.client.ApiClient

fun ApiClient.isReady(): Boolean = baseUrl != ""
    && userId != null
    && accessToken != null

fun Context.getPackageInfo(): PackageInfo {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        @Suppress("DEPRECATION") packageManager.getPackageInfo(packageName, 0)
    }
}
