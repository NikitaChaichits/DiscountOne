package com.digeltech.discountone.data.repository

import com.digeltech.discountone.data.mapper.ShopMapper
import com.digeltech.discountone.data.source.remote.api.ServerApi
import com.digeltech.discountone.domain.model.Item
import com.digeltech.discountone.domain.model.Shop
import com.digeltech.discountone.domain.repository.ShopsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ShopsRepositoryImpl @Inject constructor(
    private val api: ServerApi,
) : ShopsRepository {

    override suspend fun getAllShops(): Result<List<Shop>> = withContext(Dispatchers.IO) {
        runCatching {
            ShopMapper().map(api.getAllShops())
        }
    }

    override suspend fun getShopCategories(pageslug: String): Result<List<Item>> =
        withContext(Dispatchers.IO) {
            runCatching {
                api.getShopCategories(pageslug).map {
                    Item(0, it.name, it.slug, it.taxonomy, isParent = it.parentId == 0)
                }
            }
        }
}