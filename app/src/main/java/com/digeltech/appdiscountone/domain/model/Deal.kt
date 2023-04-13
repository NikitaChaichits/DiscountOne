package com.digeltech.appdiscountone.domain.model

data class Deal(
    val id: Int,
    val title: String,
    val description: String,
    var imageUrl: String? = null,
    val companyName: String,
    val companyLogoUrl: String? = null,
    val categoryId: Int,
    val oldPrice: Int,
    val discountPrice: Int,
    val priceCurrency: String = "₹",
    val promocode: String = "",
    val rating: Int,
    var isAddedToBookmark: Boolean = false,
    val publishedDate: String = "",
    val validDate: String = "",
)
