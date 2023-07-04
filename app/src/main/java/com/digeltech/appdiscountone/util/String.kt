package com.digeltech.appdiscountone.util

import android.content.Context
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.util.numbers.getPercent
import kotlin.math.roundToInt

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

fun getDiscountText(price: Double, discountPrice: Double): String {
    val discountSize = (price - discountPrice).getPercent(price)
    return if (discountSize > 0)
        "₹ ${discountPrice.roundToInt()} ($discountSize%)"
    else
        "₹ ${discountPrice.roundToInt()}"
}


fun getPriceString(context: Context, price: Int): String {
    return context.getString(R.string.price_wildcard, price)
}

fun String.capitalizeFirstLetter(): String {
    return substring(0, 1).uppercase() + substring(1)
}