package com.digeltech.appdiscountone.ui.categories.interactor

import com.digeltech.appdiscountone.domain.model.Category
import com.digeltech.appdiscountone.domain.model.Deal

interface CategoriesInteractor {

    suspend fun getCategoriesList(): List<Category>

    suspend fun getCategoryDealsList(categoryId: Int): List<Deal>

}