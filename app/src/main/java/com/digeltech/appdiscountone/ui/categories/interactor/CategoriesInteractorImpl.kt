package com.digeltech.appdiscountone.ui.categories.interactor

import com.digeltech.appdiscountone.domain.model.Category
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.repository.CategoriesRepository
import javax.inject.Inject

internal class CategoriesInteractorImpl @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) : CategoriesInteractor {

    override suspend fun getCategoriesList(): List<Category> = categoriesRepository.getAllCategories()

    override suspend fun getCategoryDealsList(id: Int): List<Deal> = categoriesRepository.getCategoryDealsById(id)
}