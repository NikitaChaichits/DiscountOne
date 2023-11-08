package com.digeltech.discountone.ui.categories.interactor

import com.digeltech.discountone.domain.model.Category
import com.digeltech.discountone.domain.model.Deal

interface CategoriesInteractor {

    suspend fun getCategoriesList(): Result<List<Category>>

    suspend fun searchDeals(searchText: String): List<Deal>

    suspend fun updateBookmark(userId: String, dealId: String)
}