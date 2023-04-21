package com.digeltech.appdiscountone.ui.common

import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.orhanobut.hawk.Hawk

private const val KEY = "saved-deals"

fun addToBookmark(deal: DealParcelable) {
    if (Hawk.contains(KEY)) {
        val listOfBookMark: List<DealParcelable> = Hawk.get(KEY)
        val mutableList = listOfBookMark.toMutableList()
        mutableList.add(deal)
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

fun getListOfBookmarks(): List<DealParcelable> {
    return Hawk.get(KEY)
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