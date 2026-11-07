package com.flibatsuniverse.allidroid.manager.installer.step.patching

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.github.diamondminer88.zip.ZipWriter
import com.flibatsuniverse.allidroid.manager.BuildConfig
import com.flibatsuniverse.allidroid.manager.R
import com.flibatsuniverse.allidroid.manager.domain.manager.PreferenceManager
import com.flibatsuniverse.allidroid.manager.installer.step.Step
import com.flibatsuniverse.allidroid.manager.installer.step.StepGroup
import com.flibatsuniverse.allidroid.manager.installer.step.StepRunner
import com.flibatsuniverse.allidroid.manager.installer.step.download.DownloadBaseStep
import com.flibatsuniverse.allidroid.manager.installer.utils.ArscUtil
import com.flibatsuniverse.allidroid.manager.installer.utils.ArscUtil.addColorResource
import com.flibatsuniverse.allidroid.manager.installer.utils.ArscUtil.getMainArscChunk
import com.flibatsuniverse.allidroid.manager.installer.utils.ArscUtil.getPackageChunk
import com.flibatsuniverse.allidroid.manager.installer.utils.ArscUtil.getResourceFileName
import com.flibatsuniverse.allidroid.manager.installer.utils.AxmlUtil
import com.flibatsuniverse.allidroid.manager.utils.DiscordVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import java.util.zip.ZipFile

/**
 * Replaces the existing app icons with Allidroid tinted ones
 */
class ReplaceIconStep : Step() {

    private val preferences: PreferenceManager by inject()

    val context: Context by inject()

    override val group = StepGroup.PATCHING
    override val nameRes = R.string.step_change_icon

    override suspend fun run(runner: StepRunner) {
        val baseApk = runner.getCompletedStep<DownloadBaseStep>().workingCopy

        runner.logger.i("Reading resources.arsc")
        val arsc = ArscUtil.readArsc(baseApk)
        
        val iconRscIds = AxmlUtil.readManifestIconInfo(baseApk)
        val squareIconFile = arsc.getMainArscChunk().getResourceFileName(iconRscIds.squareIcon, "anydpi-v26")
        val roundIconFile = arsc.getMainArscChunk().getResourceFileName(iconRscIds.roundIcon, "anydpi-v26")

        runner.logger.i("Patching icon assets (squareIcon=$squareIconFile, roundIcon=$roundIconFile)")

        val backgroundColor = arsc.getPackageChunk().addColorResource("brand", Color(BuildConfig.MODDED_APP_ICON))

        val postfix = when (preferences.channel) {
            DiscordVersion.Type.BETA -> "beta"
            DiscordVersion.Type.ALPHA -> "canary"
            else -> null
        }
        
        val apkEntries = withContext(Dispatchers.IO) {
            ZipFile(baseApk).use { zip -> zip.entries().asSequence().map { it.name }.toSet() }
        }

        // setOf to not possibly patch same file twice
        for (rscFile in setOf(squareIconFile, roundIconFile)) { 
            // Discord recently enabled resource shrinking, which strips the other channels' icons,
            // so some icons may not exist
            val referencePath = postfix
                ?.let { rscFile.replace("_$it.xml", ".xml") }
                ?.takeIf { it in apkEntries }
                ?: rscFile
        
            runner.logger.i("Patching adaptive icon ($rscFile <- $referencePath)")

            AxmlUtil.patchAdaptiveIcon(
                apk = baseApk,
                resourcePath = rscFile,
                referencePath = referencePath,
                backgroundColor = backgroundColor,
            )
        }

        runner.logger.i("Writing and compiling resources.arsc")
        ZipWriter(baseApk, /* append = */ true).use {
            it.deleteEntry("resources.arsc")
            it.writeEntry("resources.arsc", arsc.toByteArray())
        }
    }

}
