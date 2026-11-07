package com.flibatsuniverse.allidroid.manager.ui.viewmodel.settings

import android.content.Context
import android.os.Environment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.flibatsuniverse.allidroid.manager.BuildConfig
import com.flibatsuniverse.allidroid.manager.R
import com.flibatsuniverse.allidroid.manager.domain.manager.InstallMethod
import com.flibatsuniverse.allidroid.manager.domain.manager.PreferenceManager
import com.flibatsuniverse.allidroid.manager.domain.manager.UpdateCheckerDuration
import com.flibatsuniverse.allidroid.manager.installer.shizuku.ShizukuPermissions
import com.flibatsuniverse.allidroid.manager.updatechecker.worker.UpdateWorker
import com.flibatsuniverse.allidroid.manager.utils.showToast
import kotlinx.coroutines.launch
import java.io.File

class AdvancedSettingsViewModel(
    private val context: Context,
    private val prefs: PreferenceManager,
) : ScreenModel {
    private val cacheDir = context.externalCacheDir ?: File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS).resolve(
        BuildConfig.MANAGER_NAME).also { it.mkdirs() }

    fun clearCache() {
        cacheDir.deleteRecursively()
        context.showToast(R.string.msg_cleared_cache)
    }

    fun updateCheckerDuration(updateCheckerDuration: UpdateCheckerDuration) {
        val wm = WorkManager.getInstance(context)
        when (updateCheckerDuration) {
            UpdateCheckerDuration.DISABLED -> wm.cancelUniqueWork("com.flibatsuniverse.allidroid.manager.UPDATE_CHECK")
            else -> wm.enqueueUniquePeriodicWork(
                "com.flibatsuniverse.allidroid.manager.UPDATE_CHECK",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                PeriodicWorkRequestBuilder<UpdateWorker>(
                    updateCheckerDuration.time,
                    updateCheckerDuration.unit
                ).build()
            )
        }
    }

    fun setInstallMethod(method: InstallMethod) {
        when (method) {
            InstallMethod.SHIZUKU -> screenModelScope.launch {
                if (ShizukuPermissions.waitShizukuPermissions()) {
                    prefs.installMethod = InstallMethod.SHIZUKU
                } else {
                    context.showToast(R.string.msg_shizuku_denied)
                }
            }

            else -> prefs.installMethod = method
        }
    }

}