package com.flibatsuniverse.allidroid.manager

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.flibatsuniverse.allidroid.manager.di.httpModule
import com.flibatsuniverse.allidroid.manager.di.managerModule
import com.flibatsuniverse.allidroid.manager.di.repositoryModule
import com.flibatsuniverse.allidroid.manager.di.viewModelModule
import com.flibatsuniverse.allidroid.manager.domain.manager.PreferenceManager
import com.flibatsuniverse.allidroid.manager.domain.manager.UpdateCheckerDuration
import com.flibatsuniverse.allidroid.manager.updatechecker.worker.UpdateWorker
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ManagerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initNotificationChannels()

        startKoin {
            androidContext(this@ManagerApplication)
            modules(
                httpModule,
                managerModule,
                viewModelModule,
                repositoryModule
            )
        }

        val prefs: PreferenceManager = get()

        if (prefs.updateDuration != UpdateCheckerDuration.DISABLED) {
            val duration = prefs.updateDuration
            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                "com.flibatsuniverse.allidroid.manager.UPDATE_CHECK",
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<UpdateWorker>(duration.time, duration.unit).build()
            )
        }
    }

    private fun initNotificationChannels() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val updates = NotificationChannel(
            "${BuildConfig.APPLICATION_ID}.notifications.UPDATE",
            "Discord updates",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        nm.createNotificationChannel(updates)
    }

}