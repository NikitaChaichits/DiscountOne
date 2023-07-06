package com.digeltech.appdiscountone.ui.shops.interactor

import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.model.Shop

interface ShopsInteractor {

    suspend fun getShopsList(): Result<List<Shop>>

    suspend fun getShopDealsList(shopId: Int): Result<List<Deal>>

    suspend fun searchDeals(searchText: String): List<Deal>
}