package com.digeltech.appdiscountone.data.repository

import com.digeltech.appdiscountone.data.source.remote.DatabaseConnection
import com.digeltech.appdiscountone.domain.model.Shop
import com.digeltech.appdiscountone.domain.repository.ShopsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShopsRepositoryImpl @Inject constructor(
    private val databaseConnection: DatabaseConnection
) : ShopsRepository {

    override suspend fun getShopsList(): List<Shop> = withContext(Dispatchers.IO) {
        databaseConnection.getAllShops()
    }

}