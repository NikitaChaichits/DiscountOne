package com.digeltech.discountone

import android.app.Application
import com.facebook.appevents.AppEventsLogger
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.HiltAndroidApp
import io.branch.referral.Branch

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Hawk.init(applicationContext).build()

        // Branch logging for debugging
        Branch.enableTestMode()
        // Branch object initialization
        Branch.getAutoInstance(this)

        AppEventsLogger.activateApp(this)
    }

}