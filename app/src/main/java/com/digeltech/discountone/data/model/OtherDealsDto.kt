package com.digeltech.discountone.data.model

import com.google.gson.annotations.SerializedName

data class OtherDealsDto(
    @SerializedName("posts")
    val posts: List<DealDto>,
)
