package com.digeltech.discountone.domain.model

data class Category(
    val id: Int,
    val name: String,
    val slug: String,
    val countOfItems: Int,
    var icon: String?,
    val subcategory: List<Subcategory>
)

data class Subcategory(
    val id: Int,
    val name: String,
    val slug: String,
    val countOfItems: Int,
    var icon: String?
)

data class CategoryWithDeals(
    val id: Int,
    val name: String,
    val items: List<Deal>,
    val parentName: String = "",
    val showParentName: Boolean = false
)

data class CategoryWithSubcategories(
    val id: Int,
    val name: String,
    val subcategories: List<CategoryWithDeals>,
)