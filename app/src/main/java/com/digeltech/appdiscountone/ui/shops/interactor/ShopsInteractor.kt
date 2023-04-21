package com.digeltech.appdiscountone.ui.shops.interactor

import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.model.Shop

interface ShopsInteractor {

    suspend fun getShopsList(): List<Shop>

    suspend fun getShopDealsList(shopId: Int): List<Deal>

}