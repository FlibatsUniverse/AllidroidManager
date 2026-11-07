package com.flibatsuniverse.allidroid.manager.utils

import android.content.Context
import com.flibatsuniverse.allidroid.manager.BuildConfig
import java.io.File

object LogStore {

    private fun dir(context: Context) = context.filesDir.resolve("logs").also { it.mkdirs() }

    fun list(context: Context): List<File> =
        dir(context).listFiles { file -> file.extension == "log" }
            ?.sortedByDescending { it.lastModified() }
            .orEmpty()

    fun save(context: Context, text: String): File =
        dir(context)
            .resolve("${BuildConfig.MOD_NAME}-Manager-${System.currentTimeMillis()}.log")
            .also { it.writeText(text) }

    fun deleteAll(context: Context) {
        dir(context).deleteRecursively()
    }

    fun hasErrors(file: File): Boolean =
        file.useLines { lines -> lines.any { it.contains(" [E] ") } }
}
