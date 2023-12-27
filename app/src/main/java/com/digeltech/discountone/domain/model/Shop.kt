package com.digeltech.discountone.domain.model

data class Shop(
    val id: Int,
    val name: String,
    val slug: String,
    val countOfItems: Int,
    var icon: String?,
    val popular: Boolean,
)

data class HomeShop(
    val id: String,
    val name: String,
    var icon: String?,
    val slug: String,
)