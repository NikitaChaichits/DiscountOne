package com.digeltech.discountone.ui.categories.interactor

import com.digeltech.discountone.domain.model.Category
import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.repository.CategoriesRepository
import com.digeltech.discountone.domain.repository.DealsRepository
import javax.inject.Inject

internal class CategoriesInteractorImpl @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
    private val dealsRepository: DealsRepository,
) : CategoriesInteractor {

    override suspend fun getCategoriesList(): Result<List<Category>> = categoriesRepository.getAllCategories()

    override suspend fun getCategoryDealsList(categoryId: Int): Result<List<Deal>> =
        dealsRepository.getDealsByCategoryAndShopId(categoryId = categoryId, shopId = null)

    override suspend fun searchDeals(searchText: String): List<Deal> = dealsRepository.searchDeals(searchText)
}