package com.digeltech.discountone.util

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase


class AnalyticsLogger() {

    fun logOpenedScreen(screenName: String?, screenClass: String?) {
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }

}
