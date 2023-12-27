package com.digeltech.discountone.data.model

import com.google.gson.annotations.SerializedName

data class ShopDto(
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
    @SerializedName("meta_value")
    val popular: String,
)

data class HomeShopDto(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("image_url")
    var icon: String?,
    @SerializedName("slug")
    val slug: String?,
)