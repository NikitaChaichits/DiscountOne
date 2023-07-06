package com.digeltech.appdiscountone.ui.categories.interactor

import com.digeltech.appdiscountone.domain.model.Category
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.repository.CategoriesRepository
import com.digeltech.appdiscountone.domain.repository.DealsRepository
import javax.inject.Inject

internal class CategoriesInteractorImpl @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
    private val dealsRepository: DealsRepository,
) : CategoriesInteractor {

    override suspend fun getCategoriesList(): Result<List<Category>> = categoriesRepository.getAllCategories()

    override suspend fun getCategoryDealsList(categoryId: Int): Result<List<Deal>> =
        dealsRepository.getDealsByCategoryId(categoryId)

    override suspend fun searchDeals(searchText: String): List<Deal> = dealsRepository.searchDeals(searchText)
}