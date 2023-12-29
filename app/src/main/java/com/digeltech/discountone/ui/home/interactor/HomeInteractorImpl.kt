package com.digeltech.discountone.ui.home.interactor

import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.model.Homepage
import com.digeltech.discountone.domain.repository.DealsRepository
import javax.inject.Inject

internal class HomeInteractorImpl @Inject constructor(
    private val dealsRepository: DealsRepository,
) : HomeInteractor {

    override suspend fun getHomepage(): Result<Homepage> = dealsRepository.getHomepage()

    override suspend fun getDeal(dealId: Int): Result<Deal> = dealsRepository.getDealById(dealId = dealId)

    override suspend fun searchDeals(searchText: String): List<Deal> = dealsRepository.searchDeals(searchText)

    override suspend fun updateDealViewsClick(id: String) = dealsRepository.updateDealViewsClick(id)

    override suspend fun getFetchListOfBookmarks(userId: String): Result<List<Deal>> =
        dealsRepository.getBookmarksDeals(userId)

    override suspend fun updateBookmark(userId: String, dealId: String) = dealsRepository.updateBookmark(userId, dealId)
}