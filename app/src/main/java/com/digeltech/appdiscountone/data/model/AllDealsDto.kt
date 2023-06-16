package com.digeltech.appdiscountone.data.model

import com.google.gson.annotations.SerializedName

data class AllDealsDto(
    @SerializedName("categories")
    val categories: List<ItemDto>,
    @SerializedName("categories_shops")
    val shops: List<ItemDto>,
    @SerializedName("posts")
    val posts: List<DealDto>,
)

data class ItemDto(
    @SerializedName("term_id")
    val id: Int,
    @SerializedName("name")
    val name: String,
)