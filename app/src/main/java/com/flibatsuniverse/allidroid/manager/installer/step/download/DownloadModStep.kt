package com.flibatsuniverse.allidroid.manager.installer.step.download

import androidx.compose.runtime.Stable
import com.flibatsuniverse.allidroid.manager.R
import com.flibatsuniverse.allidroid.manager.installer.step.download.base.DownloadStep
import java.io.File

/**
 * Downloads the Allidroid XPosed module
 *
 * https://github.com/FlibatsUniverse/Allidroid
 */
@Stable
class DownloadModStep(
    workingDir: File
): DownloadStep() {

    override val nameRes = R.string.step_dl_mod

    override val downloadFullUrl: String = "https://github.com/FlibatsUniverse/Allidroid/releases/latest/download/app-release.apk"
    override val destination = preferenceManager.moduleLocation
    override val workingCopy = workingDir.resolve("xposed.apk")

}
