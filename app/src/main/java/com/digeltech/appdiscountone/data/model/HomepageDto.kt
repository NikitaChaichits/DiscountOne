package com.digeltech.appdiscountone.data.model

import com.google.gson.annotations.SerializedName

data class HomepageDto(
    @SerializedName("slider_1")
    val listOfBanners: List<DealDto>,
    @SerializedName("slider_3")
    val soloBanner: List<BannerDto>,
    @SerializedName("cat_with_posts")
    val homeCategories: List<CategoryWithItemsDto>,
)
