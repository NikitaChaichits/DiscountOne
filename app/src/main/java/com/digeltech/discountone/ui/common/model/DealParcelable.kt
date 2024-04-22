package com.digeltech.discountone.ui.common.model

import android.os.Parcelable
import com.digeltech.discountone.domain.model.Deal
import kotlinx.parcelize.Parcelize

@Parcelize
data class DealParcelable(
    val id: Int,
    val categoryId: Int,
    val parsId: Int,
    val title: String,
    val description: String,
    var imageUrl: String,
    var imagesUrl: List<String>?,
    val shopName: String,
    val shopSlug: String,
    val shopImageUrl: String?,
    val shopLink: String?,
    val oldPrice: Int,
    val price: Int,
    val priceCurrency: String = "₹",
    val promocode: String?,
    val rating: Int,
    val webLink: String?,
    var isAddedToBookmark: Boolean = false,
    val publishedDate: String?,
    val lastUpdateDate: String?,
    val expirationDate: String?,
    val sale: String?,
    val saleSize: Int,
    val viewsClick: Int,
    val dealType: DealType,
    var couponsTypeName: String? = null,
    var couponsTypeSlug: String? = null,
    var couponsCategory: String? = null
) : Parcelable

fun List<Deal>.toParcelableList(): List<DealParcelable> {
    return map { it.toParcelable() }
}

fun Deal.toParcelable(): DealParcelable {
    return DealParcelable(
        id = id,
        categoryId = categoryId,
        parsId = parsId,
        title = title,
        description = description,
        imageUrl = imageUrl,
        imagesUrl = imagesUrl,
        shopName = shopName,
        shopSlug = shopSlug,
        shopImageUrl = shopImageUrl,
        oldPrice = oldPrice,
        price = price,
        priceCurrency = priceCurrency,
        promocode = promocode,
        shopLink = shopLink,
        rating = rating,
        isAddedToBookmark = isAddedToBookmark,
        publishedDate = publishedDate,
        expirationDate = expirationDate,
        lastUpdateDate = lastUpdateDate ?: publishedDate,
        sale = sale,
        saleSize = saleSize,
        viewsClick = viewsClick,
        webLink = webLink,
        dealType = dealType,
        couponsTypeSlug = couponsTypeSlug,
        couponsTypeName = couponsTypeName,
        couponsCategory = couponsCategory
    )
}