package com.digeltech.discountone.ui.common

import android.os.Bundle
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

const val ANALYTICS_SHOP_NOW = "shop_now"
const val ANALYTICS_SHOW_CATEGORY_DEALS = "show_category_deals"
const val ANALYTICS_SHOW_SHOP_DEALS = "show_shop_deals"
const val ANALYTICS_OPEN_DEAL = "open_deal"

const val PARAMETER_URL = "url"

fun logSignUp(
    email: String,
    logger: AppEventsLogger
) {
    val params = Bundle()
    params.putString("email", email)
    Firebase.analytics.logEvent("complete_registration", params)

    val facebookParams = Bundle()
    params.putString("email", email)
    logger.logEvent("complete_registration", facebookParams)
}

fun logLogin(
    email: String,
    logger: AppEventsLogger
) {
    val params = Bundle()
    params.putString("email", email)
    Firebase.analytics.logEvent("login", params)

    val facebookParams = Bundle()
    params.putString("email", email)
    logger.logEvent("login", facebookParams)
}

fun logShopNow(
    name: String,
    url: String,
    shopName: String,
    categoryName: String,
    price: String,
    className: String,
    logger: AppEventsLogger
) {
    val params = Bundle()
    params.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
    params.putString(PARAMETER_URL, url)
    Firebase.analytics.logEvent(ANALYTICS_SHOP_NOW, params)

    val facebookParams = Bundle()
    facebookParams.putString("product_name", name)
    facebookParams.putString("partner_name", shopName)
    facebookParams.putString("brand_name", shopName)
    facebookParams.putString("category_name", categoryName)
    facebookParams.putString("price", price)
    facebookParams.putString("shopUrl", url)
    facebookParams.putString("list_name", className)
    logger.logEvent("product_checkout", facebookParams)
}

fun logOpenShopDeals(
    shopName: String,
    logger: AppEventsLogger
) {
    val params = Bundle()
    params.putString(FirebaseAnalytics.Param.ITEM_NAME, shopName)
    Firebase.analytics.logEvent(ANALYTICS_SHOW_SHOP_DEALS, params)

    val facebookParams = Bundle()
    facebookParams.putString("brand_name", shopName)
    logger.logEvent("open_shops_deals", facebookParams)
}

fun logOpenCategoryDeals(
    categoryName: String,
    logger: AppEventsLogger
) {
    val params = Bundle()
    params.putString(FirebaseAnalytics.Param.ITEM_NAME, categoryName)
    Firebase.analytics.logEvent(ANALYTICS_SHOW_CATEGORY_DEALS, params)

    val facebookParams = Bundle()
    facebookParams.putString("category_name", categoryName)
    logger.logEvent("open_category_deals", facebookParams)
}

fun logOpenDeal(
    name: String,
    shopName: String,
    categoryName: String,
    price: String,
    className: String,
    logger: AppEventsLogger
) {
    val params = Bundle()
    params.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
    Firebase.analytics.logEvent(ANALYTICS_OPEN_DEAL, params)

    val facebookParams = Bundle()
    facebookParams.putString("product_name", name)
    facebookParams.putString("partner_name", shopName)
    facebookParams.putString("brand_name", shopName)
    facebookParams.putString("category_name", categoryName)
    facebookParams.putString("price", price)
    facebookParams.putString("list_name", className)
    logger.logEvent("product_view", facebookParams)
}

fun logSearch(
    searchText: String,
    logger: AppEventsLogger
) {
    val params = Bundle()
    params.putString(FirebaseAnalytics.Param.ITEM_NAME, searchText)
    Firebase.analytics.logEvent(FirebaseAnalytics.Event.SEARCH, params)

    val facebookParams = Bundle()
    facebookParams.putString("search_string", searchText)
    logger.logEvent("product_search", facebookParams)
}