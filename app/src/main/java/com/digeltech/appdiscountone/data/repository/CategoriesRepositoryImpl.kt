package com.digeltech.appdiscountone.data.repository

import com.digeltech.appdiscountone.data.mapper.CategoryMapper
import com.digeltech.appdiscountone.data.source.remote.api.ServerApi
import com.digeltech.appdiscountone.domain.model.Category
import com.digeltech.appdiscountone.domain.repository.CategoriesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class CategoriesRepositoryImpl @Inject constructor(
    private val api: ServerApi,
) : CategoriesRepository {

    override suspend fun getAllCategories(): List<Category> = withContext(Dispatchers.IO) {
        with(api.getAllCategories()) { CategoryMapper().map(this) }
    }

}