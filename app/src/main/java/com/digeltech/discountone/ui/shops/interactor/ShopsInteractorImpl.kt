package com.digeltech.discountone.ui.shops.interactor

import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.model.Shop
import com.digeltech.discountone.domain.repository.DealsRepository
import com.digeltech.discountone.domain.repository.ShopsRepository
import javax.inject.Inject

internal class ShopsInteractorImpl @Inject constructor(
    private val shopsRepository: ShopsRepository,
    private val dealsRepository: DealsRepository,
) : ShopsInteractor {

    override suspend fun getShopsList(): Result<List<Shop>> = shopsRepository.getAllShops()

    override suspend fun searchDeals(searchText: String): List<Deal> = dealsRepository.searchDeals(searchText)

    override suspend fun updateBookmark(userId: String, dealId: String) = dealsRepository.updateBookmark(userId, dealId)
}