package com.digeltech.discountone.domain.repository

import com.digeltech.discountone.domain.model.Category
import com.digeltech.discountone.domain.model.CategoryShopFilterItem

interface CategoriesRepository {

    suspend fun getAllCategories(): Result<List<Category>>

    suspend fun getCategoryShops(pageslug: String): Result<List<CategoryShopFilterItem>>
}