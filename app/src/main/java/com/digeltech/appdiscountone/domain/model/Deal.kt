package com.digeltech.appdiscountone.domain.model

data class Deal(
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
    val rating: String,
    var isAddedToBookmark: Boolean = false,
    val publishedDate: String,
    val validDate: String,
    val sale: String,
)
