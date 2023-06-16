package com.digeltech.appdiscountone.data.repository

import com.digeltech.appdiscountone.data.mapper.ShopMapper
import com.digeltech.appdiscountone.data.source.remote.api.ServerApi
import com.digeltech.appdiscountone.domain.model.Shop
import com.digeltech.appdiscountone.domain.repository.ShopsRepository
import com.digeltech.appdiscountone.ui.common.KEY_SHOPS
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ShopsRepositoryImpl @Inject constructor(
    private val api: ServerApi,
) : ShopsRepository {

    override suspend fun getAllShops(): Result<List<Shop>> = withContext(Dispatchers.IO) {
        runCatching {
            ShopMapper().map(api.getAllShops()).also {
                Hawk.put(KEY_SHOPS, it)
            }
        }
    }
}