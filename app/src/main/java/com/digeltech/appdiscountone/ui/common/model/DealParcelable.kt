package com.digeltech.appdiscountone.ui.common.model

import android.os.Parcelable
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.util.safeToInt
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DealParcelable(
    val id: Int,
    val title: String,
    val description: String,
    var imageUrl: String? = null,
    val companyName: String,
    val companyLogoUrl: String? = null,
    val categoryId: Int,
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

fun List<Deal>.toDealParcelableList(): List<DealParcelable> {
    return map { it.toDealParcelable() }
}

fun Deal.toDealParcelable(): DealParcelable {
    return DealParcelable(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        companyName = companyName,
        companyLogoUrl = companyLogoUrl,
        categoryId = categoryId,
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


