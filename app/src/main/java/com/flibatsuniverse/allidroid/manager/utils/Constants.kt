package com.flibatsuniverse.allidroid.manager.utils

import android.os.Environment
import com.flibatsuniverse.allidroid.manager.BuildConfig

object Constants {


    val MOD_DIR = Environment.getExternalStorageDirectory().resolve(BuildConfig.MOD_NAME)

    val DUMMY_VERSION = DiscordVersion(1, 0, DiscordVersion.Type.STABLE)

    val START_TIME = System.currentTimeMillis()
}

object Intents {

    object Actions {
        const val INSTALL = "${BuildConfig.APPLICATION_ID}.intents.actions.INSTALL"
    }

    object Extras {
        const val VERSION = "${BuildConfig.APPLICATION_ID}.intents.extras.VERSION"
    }

}

object Channels {
    const val UPDATE = "${BuildConfig.APPLICATION_ID}.notifications.UPDATE"
}

data class TeamMember(
    val name: String,
    val role: String,
    val username: String = name
)