package com.digeltech.discountone

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.facebook.appevents.AppEventsLogger
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.HiltAndroidApp
import io.branch.referral.Branch

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Hawk.init(applicationContext).build()

        // Branch analytics
        Branch.enableTestMode()
        Branch.getAutoInstance(this)

        //Facebook analytics
        AppEventsLogger.activateApp(this)
        FacebookSdk.setIsDebugEnabled(true)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)
    }

}