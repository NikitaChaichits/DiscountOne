package com.digeltech.appdiscountone.ui.common.model

import android.os.Parcelable
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.util.safeToInt
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DealParcelable(
    val id: Int,
    val categoryId: Int,
    val title: String,
    val description: String,
    var imageUrl: String? = null,
    val shopName: String,
    val shopImageUrl: String? = null,
    val oldPrice: String,
    val discountPrice: String,
    val priceCurrency: String = "₹",
    val promocode: String,
    val link: String,
    val rating: Int,
    var isAddedToBookmark: Boolean = false,
    val publishedDate: String,
    val validDate: String,
    val sale: String,
) : Parcelable

fun List<Deal>.toParcelableList(): List<DealParcelable> {
    return map { it.toParcelable() }
}

fun Deal.toParcelable(): DealParcelable {
    return DealParcelable(
        id = id,
        categoryId = categoryId,
        title = title,
        description = description,
        imageUrl = imageUrl,
        shopName = shopName,
        shopImageUrl = shopImageUrl,
        oldPrice = oldPrice,
        discountPrice = discountPrice,
        priceCurrency = priceCurrency,
        promocode = promocode,
        link = link,
        rating = rating.safeToInt(),
        isAddedToBookmark = isAddedToBookmark,
        publishedDate = publishedDate,
        validDate = validDate,
        sale = sale
    )
}


