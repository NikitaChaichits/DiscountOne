package com.digeltech.discountone.ui.common

import android.os.Bundle
import com.digeltech.discountone.domain.model.Category
import com.digeltech.discountone.domain.model.Shop
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk

private const val KEY_SAVED_DEALS = "saved-deals"
const val KEY_SHOPS = "all-shops"
const val KEY_CATEGORIES = "all-categories"
const val KEY_USER = "user"

fun addToBookmark(deal: DealParcelable) {
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

fun removeFromBookmark(id: Int) {
    val listOfBookMark: List<DealParcelable> = Hawk.get(KEY_SAVED_DEALS)
    val mutableList = listOfBookMark.toMutableList()
    mutableList.find { it.id == id }?.let(mutableList::remove)

    Hawk.put(KEY_SAVED_DEALS, mutableList.toList())
}

fun getListOfBookmarks(): List<DealParcelable>? {
    if (Hawk.contains(KEY_SAVED_DEALS)) {
        return Hawk.get(KEY_SAVED_DEALS)
    }
    return emptyList()
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

fun getShopIdByName(name: String): Int {
    val listOfShops: List<Shop> = Hawk.get(KEY_SHOPS)
    return listOfShops.find {
        it.name.equals(name, true)
    }?.id ?: 0
}