package com.flibatsuniverse.allidroid.manager.updatechecker.worker

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flibatsuniverse.allidroid.manager.BuildConfig
import com.flibatsuniverse.allidroid.manager.R
import com.flibatsuniverse.allidroid.manager.domain.manager.InstallManager
import com.flibatsuniverse.allidroid.manager.domain.manager.PreferenceManager
import com.flibatsuniverse.allidroid.manager.domain.repository.RestRepository
import com.flibatsuniverse.allidroid.manager.network.utils.ApiResponse
import com.flibatsuniverse.allidroid.manager.network.utils.dataOrNull
import com.flibatsuniverse.allidroid.manager.updatechecker.UpdateNotifier
import com.flibatsuniverse.allidroid.manager.updatechecker.reciever.UpdateBroadcastReceiver
import com.flibatsuniverse.allidroid.manager.utils.DiscordVersion
import com.flibatsuniverse.allidroid.manager.utils.Intents
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UpdateWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    val api: RestRepository by inject()
    val prefs: PreferenceManager by inject()
    val installManager: InstallManager by inject()

    override suspend fun doWork(): Result {
        // Each check stands alone, so a failure in one never hides the others
        runCatching { checkManager() }
        runCatching { checkAllidroid() }
        return if (runCatching { checkDiscord() }.getOrDefault(false)) Result.success() else Result.failure()
    }

    /** Tells the user when a newer Allidroid Manager is published. */
    private suspend fun checkManager() {
        val latest = api.getLatestRelease("FlibatsUniverse/AllidroidManager").dataOrNull?.tagName?.removePrefix("v") ?: return
        if (latest == BuildConfig.VERSION_NAME || latest == prefs.notifiedManagerVersion) return

        UpdateNotifier.notify(
            context,
            UpdateNotifier.MANAGER_ID,
            context.getString(R.string.title_manager_update),
            context.getString(R.string.update_description, latest)
        )
        prefs.notifiedManagerVersion = latest
    }

    /** Tells the user when a newer Allidroid is published, which needs a reinstall to reach the Discord app. */
    private suspend fun checkAllidroid() {
        if (installManager.current == null) return

        val latest = api.getLatestRelease("FlibatsUniverse/Allidroid").dataOrNull?.tagName?.removePrefix("v") ?: return
        val installed = prefs.installedModuleVersion

        // Installed before the Manager started keeping track, so the newest release is taken as the starting point
        if (installed.isBlank()) {
            prefs.installedModuleVersion = latest
            return
        }
        if (latest == installed || latest == prefs.notifiedModuleVersion) return

        UpdateNotifier.notify(
            context,
            UpdateNotifier.ALLIDROID_ID,
            context.getString(R.string.title_allidroid_update),
            context.getString(R.string.text_allidroid_update, latest)
        )
        prefs.notifiedModuleVersion = latest
    }

    /** Returns false when the Discord versions could not be read. */
    private suspend fun checkDiscord(): Boolean {
        if (prefs.discordVersion.isNotBlank()) return true

        val res = api.getLatestDiscordVersions()
        if (res !is ApiResponse.Success) return false

        val currentVersion = DiscordVersion.fromVersionCode(installManager.current?.versionCode.toString())
        val latestVersion = res.data[prefs.channel]
        if (latestVersion == null || currentVersion == null) return false

        if (latestVersion > currentVersion) {
            context.sendBroadcast(
                Intent(context, UpdateBroadcastReceiver::class.java).apply {
                    putExtra(Intents.Extras.VERSION, latestVersion.toVersionCode())
                }
            )
        }
        return true
    }

}
