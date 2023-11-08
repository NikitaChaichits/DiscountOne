package com.digeltech.discountone.ui.categoryandshop.interactor

import com.digeltech.discountone.domain.model.CategoryShopFilterItem
import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.repository.CategoriesRepository
import com.digeltech.discountone.domain.repository.DealsRepository
import com.digeltech.discountone.domain.repository.ShopsRepository
import com.digeltech.discountone.ui.common.model.CategoryType
import com.digeltech.discountone.ui.common.model.SortBy
import com.digeltech.discountone.ui.common.model.Sorting
import com.digeltech.discountone.util.isNotNullAndNotEmpty
import javax.inject.Inject

internal class CategoryAndShopInteractorImpl @Inject constructor(
    private val dealsRepository: DealsRepository,
    private val shopsRepository: ShopsRepository,
    private val categoriesRepository: CategoriesRepository,
) : CategoryAndShopInteractor {


    override suspend fun getCategoryShops(pageslug: String): Result<List<CategoryShopFilterItem>> =
        categoriesRepository.getCategoryShops(pageslug)

    override suspend fun getShopCategories(pageslug: String): Result<List<CategoryShopFilterItem>> =
        shopsRepository.getShopCategories(pageslug)

    override suspend fun searchDeals(searchText: String): List<Deal> = dealsRepository.searchDeals(searchText)

    override suspend fun getSortingDeals(
        page: String,
        categoryType: CategoryType,
        taxSlug: String,
        sorting: Sorting,
        sortBy: SortBy,
        catOrShopSlug: String?,
        priceFrom: Int?,
        priceTo: Int?
    ): Result<List<Deal>> =
        dealsRepository.getSortingDeals(
            page = page,
            categoryType = categoryType,
            taxSlug = taxSlug,
            sorting = sorting,
            sortBy = sortBy,
            catOrShopSlug = catOrShopSlug.takeIf { it.isNotNullAndNotEmpty() },
            priceFrom = priceFrom,
            priceTo = priceTo
        )

    override suspend fun getInitialDeals(categoryType: CategoryType, id: String): Result<List<Deal>> =
        dealsRepository.getInitialDeals(categoryType, id)

    override suspend fun updateBookmark(userId: String, dealId: String) = dealsRepository.updateBookmark(userId, dealId)
}