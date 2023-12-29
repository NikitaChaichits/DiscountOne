package com.digeltech.discountone.ui.common

import android.os.Bundle
import com.digeltech.discountone.domain.model.Category
import com.digeltech.discountone.domain.model.Notification
import com.digeltech.discountone.domain.model.Shop
import com.digeltech.discountone.domain.model.User
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.util.log
import com.digeltech.discountone.util.time.getCurrentDate
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk

const val KEY_SAVED_DEALS = "saved-deals"
const val KEY_SHOPS = "all-shops"
const val KEY_CATEGORIES = "all-categories"
const val KEY_USER = "user"
const val KEY_NOTIFICATION = "notification"

fun addToBookmarkCache(deal: DealParcelable) {
    if (Hawk.contains(KEY_SAVED_DEALS)) {
        val listOfBookMark: List<DealParcelable> = Hawk.get(KEY_SAVED_DEALS)
        val mutableList = listOfBookMark.toMutableList()
        mutableList.add(deal)

        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, deal.title)
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST, params)

        Hawk.put(KEY_SAVED_DEALS, mutableList.toList())
    } else {
        Hawk.put(KEY_SAVED_DEALS, listOf(deal))
    }
}

fun removeFromBookmarkCache(id: Int) {
    val listOfBookMark: List<DealParcelable> = Hawk.get(KEY_SAVED_DEALS)
    val mutableList = listOfBookMark.toMutableList()
    mutableList.find { it.id == id }?.let(mutableList::remove)

    Hawk.put(KEY_SAVED_DEALS, mutableList.toList())
}

fun isAddedToBookmark(id: Int): Boolean {
    if (Hawk.contains(KEY_SAVED_DEALS)) {
        val listOfBookMark: List<DealParcelable> = Hawk.get(KEY_SAVED_DEALS)
        listOfBookMark.forEach {
            if (it.id == id)
                return true
        }
    }
    return false
}

fun getCategoryNameById(id: Int): String {
    if (Hawk.contains(KEY_CATEGORIES)) {
        return Hawk.get<List<Category>>(KEY_CATEGORIES).find {
            it.id == id
        }?.name ?: "Unknown category name"
    }
    return "Unknown category name"
}

fun getShopIdBySlug(slug: String): Int {
    if (Hawk.contains(KEY_SHOPS)) {
        val listOfShops: List<Shop> = Hawk.get(KEY_SHOPS)
        return listOfShops.find {
            it.slug.equals(slug, true)
        }?.id ?: 0
    }
    return 0
}

fun addNotificationToCache(title: String, text: String, data: Map<String, String>) {
    val notification = Notification(title = title, text = text, date = getCurrentDate(), data = data)
    log("Added notification to cache")
    log("$title $text")
    if (Hawk.contains(KEY_NOTIFICATION)) {
        val listOfNotifications: List<Notification> = Hawk.get(KEY_NOTIFICATION)
        val mutableList = listOfNotifications.toMutableList()
        mutableList.add(0, notification)
        Hawk.put(KEY_NOTIFICATION, mutableList)
    } else {
        Hawk.put(KEY_NOTIFICATION, listOf(notification))
    }
}

fun getNotificationsList(): List<Notification> {
    if (Hawk.contains(KEY_NOTIFICATION)) {
        return Hawk.get(KEY_NOTIFICATION) as List<Notification>
    }
    return emptyList()
}

fun updateNotification(notification: Notification) {
    val listOfNotifications: List<Notification> = Hawk.get(KEY_NOTIFICATION)
    listOfNotifications.find { it == notification }?.isRead = true
    Hawk.put(KEY_NOTIFICATION, listOfNotifications)
}

fun getUserId(): String? {
    return if (Hawk.contains(KEY_USER)) {
        val user = Hawk.get(KEY_USER) as User
        user.id
    } else null
}