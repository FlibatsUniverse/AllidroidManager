package com.flibatsuniverse.allidroid.manager.updatechecker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.flibatsuniverse.allidroid.manager.BuildConfig
import com.flibatsuniverse.allidroid.manager.R
import com.flibatsuniverse.allidroid.manager.utils.Channels

object UpdateNotifier {

    const val MANAGER_ID = 1001
    const val ALLIDROID_ID = 1002

    /** Posts an update notification that opens the Manager when tapped. */
    fun notify(context: Context, id: Int, title: String, text: String) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val launch = context.packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val builder = NotificationCompat.Builder(context, Channels.UPDATE).apply {
            setSmallIcon(R.drawable.ic_update)
            setContentTitle(title)
            setContentText(text)
            setAutoCancel(true)
            priority = NotificationCompat.PRIORITY_DEFAULT
            if (launch != null) setContentIntent(PendingIntent.getActivity(context, id, launch, PendingIntent.FLAG_IMMUTABLE))
        }
        nm.notify(id, builder.build())
    }

}
