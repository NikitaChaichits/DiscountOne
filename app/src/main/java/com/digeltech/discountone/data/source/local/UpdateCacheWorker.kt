package com.digeltech.discountone.data.source.local

import android.content.Context
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.digeltech.discountone.ui.home.KEY_BANNERS
import com.digeltech.discountone.util.log
import com.orhanobut.hawk.Hawk
import java.util.concurrent.TimeUnit

class UpdateCacheWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        Hawk.delete(KEY_BANNERS)
        log("Banners deleted")

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