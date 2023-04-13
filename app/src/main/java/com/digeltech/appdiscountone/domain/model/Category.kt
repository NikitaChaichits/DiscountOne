package com.digeltech.appdiscountone.domain.model

data class Category(
    val id: Int,
    val name: String,
    val countOfItems: Int,
    var icon: String
)

data class CategoryWithItems(
    val id: Int,
    val name: String,
    val items: List<Deal>
)