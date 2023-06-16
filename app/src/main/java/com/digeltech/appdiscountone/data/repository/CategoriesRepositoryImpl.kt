package com.digeltech.appdiscountone.data.repository

import com.digeltech.appdiscountone.data.mapper.CategoryMapper
import com.digeltech.appdiscountone.data.source.remote.api.ServerApi
import com.digeltech.appdiscountone.domain.model.Category
import com.digeltech.appdiscountone.domain.repository.CategoriesRepository
import com.digeltech.appdiscountone.ui.common.KEY_CATEGORIES
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val api: ServerApi,
) : CategoriesRepository {

    override suspend fun getAllCategories(): Result<List<Category>> = withContext(Dispatchers.IO) {
        runCatching {
            CategoryMapper().map(api.getAllCategories()).also {
                Hawk.put(KEY_CATEGORIES, it)
            }
        }
    }
}