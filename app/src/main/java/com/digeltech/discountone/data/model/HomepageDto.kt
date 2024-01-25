package com.digeltech.discountone.data.model

import com.google.gson.annotations.SerializedName

data class HomepageDto(
    @SerializedName("slider_1")
    val listOfBanners: List<DealDto>,
    @SerializedName("discounts")
    val discounts: List<CategoryWithItemsDto>,
    @SerializedName("coupons")
    val coupons: List<CategoryWithItemsDto>,
    @SerializedName("finance")
    val finance: List<CategoryWithItemsDto>,
    @SerializedName("shops")
    val shops: List<HomeShopDto>,
    @SerializedName("cat_with_posts")
    val categories: List<CategoryWithItemsDto>,
)
