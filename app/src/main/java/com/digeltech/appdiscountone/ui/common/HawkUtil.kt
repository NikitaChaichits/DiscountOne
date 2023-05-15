package com.digeltech.appdiscountone.ui.common

import android.os.Bundle
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.util.log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk

private const val KEY_SAVED_DEALS = "saved-deals"
const val KEY_LOADED_ITEMS = "all-items"

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

fun addedDealToCache(deal: Deal) {
    if (Hawk.contains(KEY_LOADED_ITEMS)) {
        val listOfDeals: List<Deal> = Hawk.get(KEY_LOADED_ITEMS)
        val mutableList = listOfDeals.toMutableList()
        mutableList.add(deal)
        log("Added deal=${deal.id} to cache. Cache size ${mutableList.size}")
        Hawk.put(KEY_LOADED_ITEMS, mutableList)
    } else {
        Hawk.put(KEY_LOADED_ITEMS, listOf(deal))
    }
}

fun getDealFromCache(dealId: Int): Deal? {
    if (Hawk.contains(KEY_LOADED_ITEMS)) {
        val listOfDeals: List<Deal> = Hawk.get(KEY_LOADED_ITEMS)
        return listOfDeals.find { it.id == dealId }
    }
    return null
}

fun getSimilarDealsFromCache(dealId: Int, categoryId: Int): List<Deal> {
    if (Hawk.contains(KEY_LOADED_ITEMS)) {
        val listOfDeals: List<Deal> = Hawk.get(KEY_LOADED_ITEMS)
        val filteredListOfDeals = mutableListOf<Deal>()

        listOfDeals.forEach {
            if (it.categoryId == categoryId && it.id != dealId)
                filteredListOfDeals.add(it)
        }
        return filteredListOfDeals.shuffled().take(5)
    }
    return emptyList()
}

fun getAllDealsFromCache(): List<Deal> {
    if (Hawk.contains(KEY_LOADED_ITEMS)) {
        return Hawk.get<List<Deal>?>(KEY_LOADED_ITEMS).sortedByDescending(Deal::id)
    }
    return emptyList()
}