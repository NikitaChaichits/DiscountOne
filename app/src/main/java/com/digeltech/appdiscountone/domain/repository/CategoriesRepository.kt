package com.digeltech.appdiscountone.domain.repository

import com.digeltech.appdiscountone.domain.model.Category
import com.digeltech.appdiscountone.domain.model.Deal

interface CategoriesRepository {

    suspend fun getAllCategories(): List<Category>

    suspend fun getCategoryDealsById(id: Int): List<Deal>
}