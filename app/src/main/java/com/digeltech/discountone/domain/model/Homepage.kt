package com.digeltech.discountone.domain.model

data class Homepage(
    val listOfBanners: List<Deal>,
    val discounts: CategoryWithDeals,
    val coupons: CategoryWithDeals,
    val finance: CategoryWithDeals,
    val shops: List<HomeShop>,
    val categories: List<CategoryWithDeals>
)
