package com.digeltech.discountone.domain.model

data class Coupons(
    val categories: List<ItemWithChild>,
    val shops: List<Item>,
    val coupons: List<Deal>,
)

data class Coupon(
    val id: Int,
    val title: String?,
    val description: String?,
    val publishedDate: String?,
    val lastUpdateDate: String?,
    val imageUrl: String?,
    val shopName: String?,
    val shopSlug: String?,
    val shopImageUrl: String?,
    val shopLink: String?,
    val price: String?,
    val saleSize: Int?,
    val promocode: String?,
    val rating: String?,
    val expirationDate: String?,
    val viewsClick: Int?,
    var isAddedToBookmark: Boolean = false,
    var couponsTypeName: String?,
    var couponsTypeSlug: String?
)

data class CouponsWithItems(
    val name: String?,
    val items: List<Coupon>?,
)