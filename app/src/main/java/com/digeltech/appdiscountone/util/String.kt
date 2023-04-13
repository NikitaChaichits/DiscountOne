package com.digeltech.appdiscountone.util

import com.digeltech.appdiscountone.util.numbers.getPercent

fun String?.isNotNullAndNotEmpty(): Boolean {
    return this != null && this.isNotEmpty()
}

fun getDiscountText(price: Int, discountPrice: Int): String {
    val discountSize = (price - discountPrice).getPercent(price)
    return "Get $discountSize%"
}