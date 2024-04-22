package com.digeltech.discountone.domain.model

import com.digeltech.discountone.ui.common.model.DealType

data class Deal(
    val id: Int,
    val categoryId: Int,
    val parsId: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    var imagesUrl: List<String>?,
    val bannerImageUrl: String? = "",
    val shopName: String,
    val shopSlug: String,
    val shopImageUrl: String,
    val shopLink: String,
    val oldPrice: Int = 0,
    val price: Int = 0,
    val priceCurrency: String = "₹",
    val promocode: String? = null,
    val webLink: String?,
    val rating: Int,
    var isAddedToBookmark: Boolean = false,
    val publishedDate: String,
    val expirationDate: String? = null,
    val lastUpdateDate: String? = null,
    val sale: String? = null,
    val saleSize: Int = 0,
    val viewsClick: Int = 0,
    val dealType: DealType = DealType.DISCOUNTS,
    var couponsTypeName: String?,
    var couponsTypeSlug: String?,
    var couponsCategory: String?
)
