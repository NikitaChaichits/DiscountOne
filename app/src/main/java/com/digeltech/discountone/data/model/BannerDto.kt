package com.digeltech.discountone.data.model

import com.google.gson.annotations.SerializedName

data class BannerDto(
    @SerializedName("image_link")
    val urlImage: String,
    @SerializedName("post_id")
    val dealId: Int?,
    @SerializedName("category_id")
    val categoryId: Int?,
)
