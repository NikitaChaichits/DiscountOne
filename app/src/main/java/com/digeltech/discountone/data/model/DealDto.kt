package com.digeltech.discountone.data.model

import com.google.gson.annotations.SerializedName

data class DealDto(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("category_id")
    val categoryId: Int?,
    @SerializedName("pars_id")
    val parsId: String?,
    @SerializedName("post_title")
    val title: String?,
    @SerializedName("post_content")
    val description: String?,
    @SerializedName("post_date")
    val publishedDate: String?,
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
    @SerializedName("shop_slug")
    val shopSlug: String?,
    @SerializedName("shop_image_url")
    val shopImageUrl: String?,
    @SerializedName("shop_link")
    val shopLink: String?,
    @SerializedName("old_price")
    val oldPrice: Double?,
    @SerializedName("price")
    val price: Double?,
    @SerializedName("sale")
    val sale: String?,
    @SerializedName("sale_size")
    val saleSize: Int?,
    @SerializedName("promocode")
    val promocode: String?,
    @SerializedName("rating")
    val rating: Int?,
    @SerializedName("expiration_date")
    val expirationDate: String?,
    @SerializedName("post_modified")
    val lastUpdateDate: String?,
    @SerializedName("views_click")
    val viewsClick: Int?,
    @SerializedName("guid")
    val webLink: String?,
    @SerializedName("post_type")
    val dealType: String?,
    @SerializedName("coupons_type_slug")
    val couponsTypeSlug: String?,
    @SerializedName("coupons_type_name")
    val couponsTypeName: String?,
    @SerializedName("categories")
    val couponsCategory: String?
)
