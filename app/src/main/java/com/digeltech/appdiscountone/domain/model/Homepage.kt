package com.digeltech.appdiscountone.domain.model

import com.digeltech.appdiscountone.ui.home.adapter.Banner

data class Homepage(
    val listOfBanners: List<Deal>,
    val soloBanner: Banner,
    val categories: List<CategoryWithDeals>
)
