package com.digeltech.appdiscountone.util

import android.content.Context
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.util.numbers.getPercent

fun String?.isNotNullAndNotEmpty(): Boolean {
    return this != null && this.isNotEmpty()
}

fun String.safeToInt(): Int {
    return try {
        toInt()
    } catch (e: NumberFormatException) {
        log(e)
        return 0
    }
}

fun getDiscountText(price: Int, discountPrice: Int): String {
    val discountSize = (price - discountPrice).getPercent(price)
    return "Get $discountSize%"
}

fun getPriceString(context: Context, price: Int): String {
    return context.getString(R.string.price_wildcard, price)
}

fun String.capitalizeFirstLetter(): String {
    return substring(0, 1).uppercase() + substring(1)
}