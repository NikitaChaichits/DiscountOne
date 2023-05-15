package com.digeltech.appdiscountone.data.source.local

import android.content.Context
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.digeltech.appdiscountone.data.source.remote.DatabaseConnection
import com.digeltech.appdiscountone.ui.home.KEY_BANNERS
import com.digeltech.appdiscountone.util.log
import com.orhanobut.hawk.Hawk
import java.util.concurrent.TimeUnit

class UpdateCacheWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        Hawk.delete(KEY_BANNERS)
        log("Banners deleted")

        val databaseConnection = DatabaseConnection()
        databaseConnection.apply {
            //обновление категорий
            getAllCategories(true)
            log("Categories updated")

            //обновление магазинов
            getAllShops(true)
            log("Shops updated")

            //обновление 6 купонов у категорий на главной странице
            updateHomeCategoriesDealsInCache()
            log("Home deals updated")
        }

        return Result.success()
    }

    companion object {
        fun scheduleNextCacheUpdate(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<UpdateCacheWorker>(
                repeatInterval = 1L,
                repeatIntervalTimeUnit = TimeUnit.DAYS
            ).build()
        }
    }

}