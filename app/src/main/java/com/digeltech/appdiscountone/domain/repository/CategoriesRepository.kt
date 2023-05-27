package com.digeltech.appdiscountone.domain.repository

import com.digeltech.appdiscountone.domain.model.Category

interface CategoriesRepository {

    suspend fun getAllCategories(): List<Category>

}