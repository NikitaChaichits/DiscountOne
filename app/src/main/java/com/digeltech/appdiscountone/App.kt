package com.digeltech.appdiscountone

import android.app.Application
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.digeltech.appdiscountone.data.source.local.UpdateCacheWorker
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Hawk.init(applicationContext).build()
        scheduleHomeCategoryUpdate(applicationContext)
    }

    private fun scheduleHomeCategoryUpdate(context: Context) {
        val workRequest = UpdateCacheWorker.scheduleNextCacheUpdate()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "cache_update",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}