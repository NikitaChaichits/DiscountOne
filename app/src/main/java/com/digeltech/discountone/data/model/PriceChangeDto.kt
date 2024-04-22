package com.digeltech.discountone.data.model

import com.google.gson.annotations.SerializedName

data class PriceChangeDto(
    @SerializedName("history")
    val history: List<PriceDto>,
)

data class PriceDto(
    @SerializedName("price")
    val price: String,
    @SerializedName("unix")
    val date: String,
)
