package com.digeltech.appdiscountone.ui.shops.interactor

import com.digeltech.appdiscountone.domain.model.Shop
import com.digeltech.appdiscountone.domain.repository.ShopsRepository
import javax.inject.Inject

internal class ShopsInteractorImpl @Inject constructor(
    private val shopsRepository: ShopsRepository
) : ShopsInteractor {

    override suspend fun getShopsList(): List<Shop> = shopsRepository.getShopsList()

}