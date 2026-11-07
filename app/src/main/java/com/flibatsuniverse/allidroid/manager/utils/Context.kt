package com.flibatsuniverse.allidroid.manager.utils

import android.content.Context
import android.net.ConnectivityManager
import android.provider.Settings
import androidx.core.content.getSystemService
import android.util.TypedValue
import androidx.annotation.AnyRes
import java.io.InputStream

/**
 * Get the raw bytes for a resource.
 * @param id The resource identifier
 * @return The resource's raw bytes as stored inside the APK
 */
fun Context.getResBytes(@AnyRes id: Int): ByteArray {
    val tValue = TypedValue()
    this.resources.getValue(
        /* id = */ id,
        /* outValue = */ tValue,
        /* resolveRefs = */ true,
    )

    val resPath = tValue.string.toString()

    return this.javaClass.classLoader
        ?.getResourceAsStream(resPath)
        ?.use(InputStream::readBytes)
        ?: error("Failed to get resource file $resPath from APK")
}

/**
 * Whether the active network is metered (mobile data or a metered Wi-Fi connection).
 */
fun Context.isNetworkMetered(): Boolean =
    getSystemService<ConnectivityManager>()?.isActiveNetworkMetered ?: false

/**
 * Whether Google Play Protect app verification is turned on and Google Play services are present.
 */
fun Context.isPlayProtectEnabled(): Boolean {
    val hasPlayServices = runCatching { packageManager.getPackageInfo("com.google.android.gms", 0) }.isSuccess
    return hasPlayServices && Settings.Global.getInt(contentResolver, "package_verifier_enable", 1) == 1
}
