package com.digeltech.appdiscountone.domain.model

data class Deal(
    val id: Int,
    val categoryId: Int,
    val title: String,
    val description: String,
    var imageUrl: String,
    val shopName: String,
    val shopImageUrl: String,
    val shopLink: String,
    val oldPrice: String? = null,
    val price: String? = null,
    val priceCurrency: String = "₹",
    val promocode: String? = null,
    val webLink: String?,
    val rating: String,
    var isAddedToBookmark: Boolean = false,
    val publishedDate: String,
    val expirationDate: String? = null,
    val sale: String? = null,
    val saleSize: Int = 0,
    val viewsClick: Int = 0,
)
