package com.digeltech.appdiscountone.data.repository

import com.digeltech.appdiscountone.data.mapper.DealsMapper
import com.digeltech.appdiscountone.data.mapper.HomepageMapper
import com.digeltech.appdiscountone.data.source.remote.api.ServerApi
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.model.Homepage
import com.digeltech.appdiscountone.domain.repository.DealsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DealsRepositoryImpl @Inject constructor(
    private val api: ServerApi,
) : DealsRepository {

    override suspend fun getAllDeals(): List<Deal> = withContext(Dispatchers.IO) {
        DealsMapper().mapAllDeal(api.getAllDeals())
    }

    override suspend fun getAllCoupons(): List<Deal> = withContext(Dispatchers.IO) {
        DealsMapper().mapAllDeal(api.getAllCoupons())
    }

    override suspend fun getDealsByCategoryId(categoryId: Int): List<Deal> = withContext(Dispatchers.IO) {
        DealsMapper().mapAllDeal(api.getCategoryDeals(categoryId.toString()))
    }

    override suspend fun getDealsByShopId(shopId: Int): List<Deal> = withContext(Dispatchers.IO) {
        DealsMapper().mapAllDeal(api.getShopDeals(shopId.toString()))
    }

    override suspend fun getDealById(dealId: Int, categoryId: Int): Deal = withContext(Dispatchers.IO) {
        DealsMapper().mapToDeal(api.getDeal(dealId.toString()))
    }

    override suspend fun getHomepage(): Homepage = withContext(Dispatchers.IO) {
        HomepageMapper().map(api.getHomepage())
    }

    override suspend fun searchDeals(searchText: String): List<Deal> = withContext(Dispatchers.IO) {
        DealsMapper().mapAllDeal(api.searchDeals(searchText))
    }

}