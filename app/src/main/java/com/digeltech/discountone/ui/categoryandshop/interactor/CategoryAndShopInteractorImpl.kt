package com.digeltech.discountone.ui.categoryandshop.interactor

import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.model.Item
import com.digeltech.discountone.domain.repository.CategoriesRepository
import com.digeltech.discountone.domain.repository.DealsRepository
import com.digeltech.discountone.domain.repository.ShopsRepository
import com.digeltech.discountone.ui.common.model.DealType
import com.digeltech.discountone.ui.common.model.SortBy
import com.digeltech.discountone.ui.common.model.Taxonomy
import javax.inject.Inject

internal class CategoryAndShopInteractorImpl @Inject constructor(
    private val dealsRepository: DealsRepository,
    private val shopsRepository: ShopsRepository,
    private val categoriesRepository: CategoriesRepository,
) : CategoryAndShopInteractor {


    override suspend fun getCategoryShops(pageslug: String): Result<List<Item>> =
        categoriesRepository.getCategoryShops(pageslug)

    override suspend fun getShopCategories(pageslug: String): Result<List<Item>> =
        shopsRepository.getShopCategories(pageslug)

    override suspend fun searchDeals(searchText: String): List<Deal> = dealsRepository.searchDeals(searchText)

    override suspend fun getSortingDeals(
        page: String,
        dealType: DealType?,
        sortBy: SortBy?,
        categorySlug: String?,
        shopSlug: String?,
        taxonomy: String?,
    ): Result<List<Deal>> =
        dealsRepository.getSortingDeals(
            page = page,
            dealType = dealType,
            sortBy = sortBy,
            categorySlug = categorySlug,
            shopSlug = shopSlug,
            taxonomy = taxonomy
        )

    override suspend fun getInitialDeals(categoryType: Taxonomy, id: String): Result<List<Deal>> =
        dealsRepository.getInitialDeals(categoryType, id)

    override suspend fun updateBookmark(userId: String, dealId: String) = dealsRepository.updateBookmark(userId, dealId)

    override suspend fun updateDealViewsClick(id: String) = dealsRepository.updateDealViewsClick(id)
}