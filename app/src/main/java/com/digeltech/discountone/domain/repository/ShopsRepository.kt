package com.digeltech.discountone.domain.repository

import com.digeltech.discountone.domain.model.Item
import com.digeltech.discountone.domain.model.Shop

interface ShopsRepository {

    suspend fun getAllShops(): Result<List<Shop>>

    suspend fun getShopCategories(pageslug: String): Result<List<Item>>

}