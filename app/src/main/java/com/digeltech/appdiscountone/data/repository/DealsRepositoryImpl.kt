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

    override suspend fun getDealsById(id: Int): List<Deal> = withContext(Dispatchers.IO) {
        databaseConnection.getDealsById(id)
    }

    override suspend fun getSimilarDeals(dealId: Int, categoryId: Int): List<Deal> = withContext(Dispatchers.IO) {
        databaseConnection.getSimilarDeals(dealId = dealId, categoryId = categoryId)
    }

    override suspend fun getDealById(dealId: Int, categoryId: Int): Deal = withContext(Dispatchers.IO) {
        databaseConnection.getDealById(dealId = dealId, categoryId = categoryId)
    }

    override suspend fun getBanners(): List<Banner> = withContext(Dispatchers.IO) {
        databaseConnection.getBanners()
    }

}