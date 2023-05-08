package com.digeltech.appdiscountone.data.repository

import com.digeltech.appdiscountone.data.source.remote.DatabaseConnection
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.repository.DealsRepository
import com.digeltech.appdiscountone.ui.home.adapter.Banner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DealsRepositoryImpl @Inject constructor(
    private val databaseConnection: DatabaseConnection,
) : DealsRepository {

    override suspend fun getAllDeals(limit: Int, offset: Int): List<Deal> = withContext(Dispatchers.IO) {
        databaseConnection.getAllDeals(limit, offset)
    }

    override suspend fun getAllCoupons(limit: Int, offset: Int): List<Deal> = withContext(Dispatchers.IO) {
        databaseConnection.getAllCoupons(limit, offset)
    }

    override suspend fun getDealsByCategoryId(categoryId: Int, limit: Int, offset: Int): List<Deal> =
        withContext(Dispatchers.IO) {
            databaseConnection.getDealsById(categoryId, limit, offset)
        }

    override suspend fun getDealById(dealId: Int, categoryId: Int): Deal = withContext(Dispatchers.IO) {
        databaseConnection.getDeal(dealId = dealId, categoryId = categoryId)
    }

    override suspend fun getBanners(): List<Banner> = withContext(Dispatchers.IO) {
        databaseConnection.getBanners()
    }

}