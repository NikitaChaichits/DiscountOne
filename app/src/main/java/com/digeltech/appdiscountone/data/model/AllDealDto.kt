package com.digeltech.appdiscountone.data.model

import com.google.gson.annotations.SerializedName

data class AllDealDto(
    @SerializedName("post")
    val post: PostDto,
    @SerializedName("postmeta")
    val postMeta: PostMetaDto
)

data class PostDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("post_title")
    val title: String,
    @SerializedName("post_content")
    val description: String,
    @SerializedName("post_date")
    val publishedDate: String,
    @SerializedName("views_click")
    val viewClick: String,
)

data class PostMetaDto(
    @SerializedName("id_cat")
    val categoryId: Int,
    @SerializedName("img_url")
    var imageUrl: String,
    @SerializedName("source")
    val shopName: String,
    @SerializedName("image_shop")
    val shopImageUrl: String,
    @SerializedName("old_price")
    val oldPrice: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("promocode")
    val promocode: String?,
    @SerializedName("link_shop")
    val link: String,
    @SerializedName("rating")
    val rating: String,
    @SerializedName("expiration_date")
    val validDate: String?,
)