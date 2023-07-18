package com.digeltech.discountone.domain.model

data class Shop(
    val id: Int,
    val name: String,
    val slug: String,
    val countOfItems: Int,
    var icon: String?,
    val popular: Boolean,
)
