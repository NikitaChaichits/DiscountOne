package com.digeltech.appdiscountone.ui.common

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

const val ANALYTICS_SHOP_NOW = "shop_now"
const val ANALYTICS_SHOW_CATEGORY_DEALS = "show_category_deals"
const val ANALYTICS_SHOW_SHOP_DEALS = "show_shop_deals"
const val ANALYTICS_OPEN_DEAL = "open_deal"

const val PARAMETER_URL = "url"

fun logSignUp(email: String) {
    val params = Bundle()
    params.putString(FirebaseAnalytics.Param.ITEM_NAME, email)
    Firebase.analytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, params)
}

fun logLogin(email: String) {
    val params = Bundle()
    params.putString(FirebaseAnalytics.Param.ITEM_NAME, email)
    Firebase.analytics.logEvent(FirebaseAnalytics.Event.LOGIN, params)
}

fun logShopNow(name: String, url: String) {
    val params = Bundle()
    params.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
    params.putString(PARAMETER_URL, url)
    Firebase.analytics.logEvent(ANALYTICS_SHOP_NOW, params)
}

fun logOpenShopDeals(shopName: String) {
    val params = Bundle()
    params.putString(FirebaseAnalytics.Param.ITEM_NAME, shopName)
    Firebase.analytics.logEvent(ANALYTICS_SHOW_SHOP_DEALS, params)
}

fun logOpenCategoryDeals(categoryName: String) {
    val params = Bundle()
    params.putString(FirebaseAnalytics.Param.ITEM_NAME, categoryName)
    Firebase.analytics.logEvent(ANALYTICS_SHOW_CATEGORY_DEALS, params)
}

fun logOpenDeal(name: String) {
    val params = Bundle()
    params.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
    Firebase.analytics.logEvent(ANALYTICS_OPEN_DEAL, params)
}

fun logSearch(searchText: String) {
    val params = Bundle()
    params.putString(FirebaseAnalytics.Param.ITEM_NAME, searchText)
    Firebase.analytics.logEvent(FirebaseAnalytics.Event.SEARCH, params)
}