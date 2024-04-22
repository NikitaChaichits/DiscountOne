package com.digeltech.discountone.domain.repository

import com.digeltech.discountone.domain.model.AllDeals
import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.model.Homepage
import com.digeltech.discountone.domain.model.Price
import com.digeltech.discountone.ui.common.model.DealType
import com.digeltech.discountone.ui.common.model.SortBy
import com.digeltech.discountone.ui.common.model.Taxonomy

interface DealsRepository {

    suspend fun getDiscounts(): Result<AllDeals>

    suspend fun getBestDeals(): Result<AllDeals>

    suspend fun getAllDeals(page: String, limit: String): Result<List<Deal>>

    suspend fun getAllCoupons(): Result<List<Deal>>

    suspend fun getDealById(dealId: Int): Result<Deal>

    suspend fun getHomepage(): Result<Homepage>

    suspend fun searchDeals(searchText: String): List<Deal>

    suspend fun updateDealViewsClick(id: String)
    suspend fun activateDealClick(id: String)

    suspend fun getSimilarDealsByCategory(categoryName: String): Result<List<Deal>>

    suspend fun getSimilarCouponsByCategory(): Result<List<Deal>>

    suspend fun getSimilarDealsByShop(shopName: String): Result<List<Deal>>

    suspend fun getSimilarCouponsByShop(shopName: String): Result<List<Deal>>

    suspend fun getSortingDeals(
        page: String = "1",
        dealType: DealType? = null,
        sortBy: SortBy? = null,
        categorySlug: String? = null,
        shopSlug: String? = null,
        taxonomy: String? = null,
        pageUrl: String? = null,
    ): Result<List<Deal>>

    suspend fun getInitialDeals(
        categoryType: Taxonomy,
        id: String
    ): Result<List<Deal>>

    suspend fun getBookmarksDeals(userId: String): Result<List<Deal>>

    suspend fun updateBookmark(userId: String, dealId: String)

    suspend fun getPriceChanges(parsId: String): Result<List<Price>>
}