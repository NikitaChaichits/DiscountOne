package com.digeltech.discountone.data.model

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("term_id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("count")
    val countOfItems: Int,
    @SerializedName("image_url")
    var icon: String,
    @SerializedName("child")
    var subcategory: List<SubcategoryDto>,
)

data class SubcategoryDto(
    @SerializedName("term_id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("count")
    val countOfItems: Int,
    @SerializedName("image_url")
    var icon: String,
)

data class CategoryWithItemsDto(
    @SerializedName("category_id")
    val id: Int,
    @SerializedName("category_name")
    val name: String,
    @SerializedName("post")
    val items: List<DealDto>,
)