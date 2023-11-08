package com.digeltech.discountone.ui.categoryandshop.interactor

import com.digeltech.discountone.domain.model.CategoryShopFilterItem
import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.ui.common.model.CategoryType
import com.digeltech.discountone.ui.common.model.SortBy
import com.digeltech.discountone.ui.common.model.Sorting

interface CategoryAndShopInteractor {

    suspend fun getCategoryShops(pageslug: String): Result<List<CategoryShopFilterItem>>

    suspend fun getShopCategories(pageslug: String): Result<List<CategoryShopFilterItem>>

    suspend fun searchDeals(searchText: String): List<Deal>

    suspend fun getSortingDeals(
        page: String = "1",
        categoryType: CategoryType,
        taxSlug: String,
        sorting: Sorting,
        sortBy: SortBy,
        catOrShopSlug: String?,
        priceFrom: Int?,
        priceTo: Int?
    ): Result<List<Deal>>

    suspend fun getInitialDeals(
        categoryType: CategoryType,
        id: String
    ): Result<List<Deal>>

    suspend fun updateBookmark(userId: String, dealId: String)
}