package com.digeltech.discountone.data.model

import com.google.gson.annotations.SerializedName

data class CouponsDto(
    @SerializedName("categories")
    val categories: List<ItemWithChildDto>,
    @SerializedName("categories_shops")
    val shops: List<ItemDto>,
    @SerializedName("coupons")
    val coupons: List<DealDto>,
)