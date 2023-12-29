package com.digeltech.discountone.util.numbers

import android.content.Context
import android.icu.text.NumberFormat
import com.digeltech.discountone.util.locale.currentDeviceLocale

fun Double.toPriceString(context: Context): String {
    val locale = context.currentDeviceLocale()
    val priceNumberFormatter: NumberFormat = NumberFormat.getNumberInstance(locale)
        .apply {
            maximumFractionDigits = 2
        }
    return priceNumberFormatter.format(this)
}

fun Int.getPercent(divideTo: Int): Int {
    return if (divideTo == 0) 0
    else (this * 100 / divideTo)
}


