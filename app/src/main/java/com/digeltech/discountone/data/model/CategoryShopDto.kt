package com.digeltech.discountone.data.model

import com.google.gson.annotations.SerializedName

data class CategoryShopDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String
)
