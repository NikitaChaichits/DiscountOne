package com.digeltech.discountone.domain.model

data class Homepage(
    val listOfBanners: List<Deal>,
//    val soloBanner: Banner,
    val bestDeals: CategoryWithDeals,
    val categories: List<CategoryWithSubcategories>
)
