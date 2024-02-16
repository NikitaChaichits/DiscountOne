package com.digeltech.discountone.ui.shops.interactor

import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.model.Shop

interface ShopsInteractor {

    suspend fun getShopsList(): Result<List<Shop>>

    suspend fun searchDeals(searchText: String): List<Deal>

    suspend fun updateBookmark(userId: String, dealId: String)

    suspend fun updateDealViewsClick(id: String)
}