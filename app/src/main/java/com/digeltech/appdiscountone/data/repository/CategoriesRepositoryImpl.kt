package com.digeltech.appdiscountone.data.repository

import com.digeltech.appdiscountone.data.source.remote.DatabaseConnection
import com.digeltech.appdiscountone.domain.model.Category
import com.digeltech.appdiscountone.domain.model.CategoryWithDeals
import com.digeltech.appdiscountone.domain.repository.CategoriesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class CategoriesRepositoryImpl @Inject constructor(
    private val databaseConnection: DatabaseConnection,
) : CategoriesRepository {

    override suspend fun getAllCategories(): List<Category> = withContext(Dispatchers.IO) {
        databaseConnection.getAllCategories()
    }


    override suspend fun getHomeCategories(): List<CategoryWithDeals> = withContext(Dispatchers.IO) {
        databaseConnection.getHomeCategories()
    }
}