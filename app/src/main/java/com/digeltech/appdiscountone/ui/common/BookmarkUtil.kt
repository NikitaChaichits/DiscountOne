package com.digeltech.appdiscountone.ui.common

import android.os.Bundle
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk

private const val KEY = "saved-deals"

fun addToBookmark(deal: DealParcelable) {
    if (Hawk.contains(KEY)) {
        val listOfBookMark: List<DealParcelable> = Hawk.get(KEY)
        val mutableList = listOfBookMark.toMutableList()
        mutableList.add(deal)

        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, deal.title)
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST, params)

        Hawk.put(KEY, mutableList.toList())
    } else {
        Hawk.put(KEY, listOf(deal))
    }
}

fun removeFromBookmark(id: Int) {
    val listOfBookMark: List<DealParcelable> = Hawk.get(KEY)
    val mutableList = listOfBookMark.toMutableList()
    mutableList.find { it.id == id }?.let(mutableList::remove)

    Hawk.put(KEY, mutableList.toList())
}

fun getListOfBookmarks(): List<DealParcelable>? {
    if (Hawk.contains(KEY)) {
        return Hawk.get(KEY)
    }
    return emptyList()
}

fun isAddedToBookmark(id: Int): Boolean {
    if (Hawk.contains(KEY)) {
        val listOfBookMark: List<DealParcelable> = Hawk.get(KEY)
        listOfBookMark.forEach {
            if (it.id == id)
                return true
        }
    }
    return false
}