package com.digeltech.discountone.util

import android.content.Context
import com.digeltech.discountone.R
import com.digeltech.discountone.util.numbers.getPercent
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

fun getDiscountText(price: Double, discountPrice: Double, saleSize: Int): String {
    val discountSize = if (saleSize == 0)
        (price - discountPrice).getPercent(price)
    else saleSize

    return if (discountSize > 0)
        "₹${discountPrice.roundToInt()} ($discountSize%)"
    else
        "₹${discountPrice.roundToInt()}"
}


fun getPriceString(context: Context, price: Int): String {
    return context.getString(R.string.price_wildcard, price)
}

fun String.capitalizeFirstLetter(): String {
    return substring(0, 1).uppercase() + substring(1)
}