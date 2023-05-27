package com.digeltech.appdiscountone.data.repository

import com.digeltech.appdiscountone.data.mapper.ShopMapper
import com.digeltech.appdiscountone.data.source.remote.api.ServerApi
import com.digeltech.appdiscountone.domain.model.Shop
import com.digeltech.appdiscountone.domain.repository.ShopsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ShopsRepositoryImpl @Inject constructor(
    private val api: ServerApi,
) : ShopsRepository {

    override suspend fun getAllShops(): List<Shop> = withContext(Dispatchers.IO) {
        with(api.getAllShops()) { ShopMapper().map(this) }
    }
}