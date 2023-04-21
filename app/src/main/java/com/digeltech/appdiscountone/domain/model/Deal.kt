package com.digeltech.appdiscountone.domain.model

data class Deal(
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
    val sale: String,
    val promocode: String,
    val link: String,
    val rating: String,
    var isAddedToBookmark: Boolean = false,
    val publishedDate: String,
    val validDate: String,
)
