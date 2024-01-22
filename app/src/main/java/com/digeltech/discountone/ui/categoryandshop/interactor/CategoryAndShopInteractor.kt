package com.digeltech.discountone.ui.categoryandshop.interactor

import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.model.Item
import com.digeltech.discountone.ui.common.model.DealType
import com.digeltech.discountone.ui.common.model.SortBy
import com.digeltech.discountone.ui.common.model.Taxonomy

interface CategoryAndShopInteractor {

    suspend fun getCategoryShops(pageslug: String): Result<List<Item>>

    suspend fun getShopCategories(pageslug: String): Result<List<Item>>

    suspend fun searchDeals(searchText: String): List<Deal>

    suspend fun getSortingDeals(
        page: String = "1",
        dealType: DealType?,
        sortBy: SortBy?,
        categorySlug: String?,
        shopSlug: String?,
        taxonomy: String?,
    ): Result<List<Deal>>

    suspend fun getInitialDeals(
        categoryType: Taxonomy,
        id: String
    ): Result<List<Deal>>

    suspend fun updateBookmark(userId: String, dealId: String)

    suspend fun updateDealViewsClick(id: String)
}