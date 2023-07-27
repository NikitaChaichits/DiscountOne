package com.digeltech.discountone.data.model

import com.google.gson.annotations.SerializedName

data class DealDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("post_title")
    val title: String,
    @SerializedName("post_content")
    val description: String?,
    @SerializedName("post_date")
    val publishedDate: String,
    @SerializedName("image_url")
    val imageUrl: String?, // main image
    @SerializedName("images_url")
    val imagesUrl: List<String>?, // images on deal details screen
    @SerializedName("image_link")
    val bannerImageUrl: String?,
    @SerializedName("shop_id")
    val shopId: String?,
    @SerializedName("shop_name")
    val shopName: String?,
    @SerializedName("shop_image_url")
    val shopImageUrl: String?,
    @SerializedName("shop_link")
    val shopLink: String?,
    @SerializedName("old_price")
    val oldPrice: String?,
    @SerializedName("price")
    val price: String?,
    @SerializedName("sale")
    val sale: String?,
    @SerializedName("sale_size")
    val saleSize: Int?,
    @SerializedName("promocode")
    val promocode: String?,
    @SerializedName("rating")
    val rating: String?,
    @SerializedName("expiration_date")
    val expirationDate: String?,
    @SerializedName("views_click")
    val viewsClick: Int?,
    @SerializedName("guid")
    val webLink: String?,
)
