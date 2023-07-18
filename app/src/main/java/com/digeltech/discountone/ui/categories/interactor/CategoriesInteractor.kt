package com.digeltech.discountone.ui.categories.interactor

import com.digeltech.discountone.domain.model.Category
import com.digeltech.discountone.domain.model.Deal

interface CategoriesInteractor {

    suspend fun getCategoriesList(): Result<List<Category>>

    suspend fun getCategoryDealsList(categoryId: Int): Result<List<Deal>>

    suspend fun searchDeals(searchText: String): List<Deal>
}