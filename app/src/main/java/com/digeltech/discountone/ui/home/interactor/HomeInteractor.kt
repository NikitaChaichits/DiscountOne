package com.digeltech.discountone.ui.home.interactor

import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.model.Homepage

interface HomeInteractor {

    suspend fun getHomepage(): Result<Homepage>

    suspend fun getDeal(dealId: Int): Result<Deal>

    suspend fun searchDeals(searchText: String): List<Deal>

    suspend fun updateDealViewsClick(id: String)

    suspend fun getFetchListOfBookmarks(userId: String): Result<List<Deal>>

    suspend fun updateBookmark(userId: String, dealId: String)
}