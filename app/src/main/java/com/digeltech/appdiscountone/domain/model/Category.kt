package com.digeltech.appdiscountone.domain.model

data class Category(
    val id: Int,
    val name: String,
    val slug: String,
    val countOfItems: Int,
    var icon: String?
)

data class CategoryWithDeals(
    val id: Int,
    val name: String,
    val items: List<Deal>
)