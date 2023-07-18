package com.digeltech.discountone

import android.app.Application
import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.digeltech.discountone.data.source.local.UpdateCacheWorker
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Hawk.init(applicationContext).build()
//        scheduleHomeCategoryUpdate(applicationContext)
    }

    private fun scheduleHomeCategoryUpdate(context: Context) {
        val workRequest = UpdateCacheWorker.scheduleNextCacheUpdate()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "cache_update",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun isDarkThemeEnabled(context: Context): Boolean {
        val currentNightMode = context.resources.configuration.uiMode and UI_MODE_NIGHT_MASK
        return currentNightMode == UI_MODE_NIGHT_YES
    }
}