package com.digeltech.appdiscountone.domain.repository

import com.digeltech.appdiscountone.domain.model.Category
import com.digeltech.appdiscountone.domain.model.CategoryWithDeals

interface CategoriesRepository {

    suspend fun getAllCategories(): List<Category>

    suspend fun getInitHomeCategories(): List<CategoryWithDeals>

    suspend fun getAllHomeCategories(): List<CategoryWithDeals>

}