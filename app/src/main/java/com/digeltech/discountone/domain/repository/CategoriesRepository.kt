package com.digeltech.discountone.domain.repository

import com.digeltech.discountone.domain.model.Category

interface CategoriesRepository {

    suspend fun getAllCategories(): Result<List<Category>>

    suspend fun getCategoryShops(pageslug: String): Result<List<String>>
}