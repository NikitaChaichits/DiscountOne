package com.digeltech.discountone.ui.categories.categoryandshop.interactor

import com.digeltech.discountone.domain.model.CategoryShopFilterItem
import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.ui.common.model.CategoryType
import com.digeltech.discountone.ui.common.model.SortBy
import com.digeltech.discountone.ui.common.model.Sorting

interface CategoryAndShopInteractor {

    suspend fun getCategoryShops(pageslug: String): Result<List<CategoryShopFilterItem>>

    suspend fun getShopCategories(pageslug: String): Result<List<CategoryShopFilterItem>>

    suspend fun searchDeals(searchText: String): List<Deal>

    suspend fun updateDealViewsClick(id: String)

    suspend fun getSortingDeals(
        page: String,
        categoryType: CategoryType,
        taxSlug: String,
        sorting: Sorting,
        sortBy: SortBy,
        catOrShopSlug: String?,
        priceFrom: Int?,
        priceTo: Int?
    ): Result<List<Deal>>

}