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
    var imageUrl: String,
    val shopName: String,
    val shopImageUrl: String,
    val shopLink: String,
    val oldPrice: String?,
    val price: String?,
    val priceCurrency: String = "₹",
    val promocode: String?,
    val rating: Int,
    val webLink: String?,
    var isAddedToBookmark: Boolean = false,
    val publishedDate: String,
    val expirationDate: String?,
    val sale: String?,
    val saleSize: Int,
    val viewsClick: Int,
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
        price = price,
        priceCurrency = priceCurrency,
        promocode = promocode,
        shopLink = shopLink,
        rating = rating.safeToInt(),
        isAddedToBookmark = isAddedToBookmark,
        publishedDate = publishedDate,
        expirationDate = expirationDate,
        sale = sale,
        saleSize = saleSize,
        viewsClick = viewsClick,
        webLink = webLink,
    )
}


