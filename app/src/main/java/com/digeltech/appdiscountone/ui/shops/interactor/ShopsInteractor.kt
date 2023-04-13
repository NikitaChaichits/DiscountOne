package com.digeltech.appdiscountone.ui.shops.interactor

import com.digeltech.appdiscountone.domain.model.Shop

interface ShopsInteractor {

    suspend fun getShopsList(): List<Shop>

}