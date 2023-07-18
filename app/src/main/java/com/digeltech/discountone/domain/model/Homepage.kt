package com.digeltech.discountone.domain.model

import com.digeltech.discountone.ui.home.adapter.Banner

data class Homepage(
    val listOfBanners: List<Deal>,
    val soloBanner: Banner,
    val bestDeals: CategoryWithDeals,
    val categories: List<CategoryWithSubcategories>
)
