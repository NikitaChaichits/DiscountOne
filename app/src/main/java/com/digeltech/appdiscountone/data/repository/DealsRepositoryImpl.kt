package com.digeltech.appdiscountone.data.repository

import com.digeltech.appdiscountone.data.mapper.DealsMapper
import com.digeltech.appdiscountone.data.mapper.HomepageMapper
import com.digeltech.appdiscountone.data.source.remote.api.ServerApi
import com.digeltech.appdiscountone.domain.model.AllDeals
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.model.Homepage
import com.digeltech.appdiscountone.domain.repository.DealsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DealsRepositoryImpl @Inject constructor(
    private val api: ServerApi,
) : DealsRepository {

    override suspend fun getAllDeals(): Result<AllDeals> = withContext(Dispatchers.IO) {
        runCatching {
            DealsMapper().mapAllDeals(api.getBestDeals())
        }
    }

    override suspend fun getAllCoupons(): Result<List<Deal>> = withContext(Dispatchers.IO) {
        runCatching {
            DealsMapper().mapDeals(api.getAllCoupons())
        }
    }

    override suspend fun getDealsByCategoryId(categoryId: Int): List<Deal> = withContext(Dispatchers.IO) {
        DealsMapper().mapDeals(api.getCategoryDeals(categoryId.toString()))
    }

    override suspend fun getDealsByShopId(shopId: Int): List<Deal> = withContext(Dispatchers.IO) {
        DealsMapper().mapDeals(api.getShopDeals(shopId.toString()))
    }

    override suspend fun getDealById(dealId: Int, categoryId: Int): Deal = withContext(Dispatchers.IO) {
        DealsMapper().mapToDeal(api.getDeal(dealId.toString()))
    }

    override suspend fun getHomepage(): Result<Homepage> = withContext(Dispatchers.IO) {
        runCatching {
            HomepageMapper().map(api.getHomepage())
        }
    }

    override suspend fun searchDeals(searchText: String): List<Deal> = withContext(Dispatchers.IO) {
        DealsMapper().mapDeals(api.searchDeals(searchText))
    }

    override suspend fun updateDealViewsClick(id: String) = withContext(Dispatchers.IO) {
        api.updateViewsClick(id)
    }

    override suspend fun getSimilarDealsByCategory(categoryName: String): List<Deal> = withContext(Dispatchers.IO) {
        DealsMapper().mapOtherDeals(api.getTopDeals(categoryName))
    }

    override suspend fun getSimilarDealsByShop(shopName: String): List<Deal> = withContext(Dispatchers.IO) {
        DealsMapper().mapOtherDeals(api.getOtherDeals(shopName))
    }

}