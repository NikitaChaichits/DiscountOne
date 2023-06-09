package com.digeltech.appdiscountone.domain.repository

import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.model.Homepage

interface DealsRepository {
    suspend fun getAllDeals(): List<Deal>

    suspend fun getAllCoupons(): List<Deal>

    suspend fun getDealsByCategoryId(categoryId: Int): List<Deal>

    suspend fun getDealsByShopId(shopId: Int): List<Deal>

    suspend fun getDealById(dealId: Int, categoryId: Int): Deal

    suspend fun getHomepage(): Homepage

    suspend fun searchDeals(searchText: String): List<Deal>

    suspend fun updateDealViewsClick(id: String)
}