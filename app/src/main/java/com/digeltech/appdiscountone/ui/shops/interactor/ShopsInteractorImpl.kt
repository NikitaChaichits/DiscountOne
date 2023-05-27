package com.digeltech.appdiscountone.ui.shops.interactor

import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.model.Shop
import com.digeltech.appdiscountone.domain.repository.DealsRepository
import com.digeltech.appdiscountone.domain.repository.ShopsRepository
import javax.inject.Inject

internal class ShopsInteractorImpl @Inject constructor(
    private val shopsRepository: ShopsRepository,
    private val dealsRepository: DealsRepository,
) : ShopsInteractor {

    override suspend fun getShopsList(): List<Shop> = shopsRepository.getAllShops()

    override suspend fun getShopDealsList(shopId: Int): List<Deal> = dealsRepository.getDealsByShopId(shopId)
}