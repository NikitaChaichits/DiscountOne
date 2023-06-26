package com.digeltech.appdiscountone.data.model

import com.google.gson.annotations.SerializedName

data class OtherDealsDto(
    @SerializedName("posts")
    val posts: List<DealDto>,
)
