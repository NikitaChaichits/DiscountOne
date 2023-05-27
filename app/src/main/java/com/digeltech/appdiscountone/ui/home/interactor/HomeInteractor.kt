package com.digeltech.appdiscountone.ui.home.interactor

import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.model.Homepage

interface HomeInteractor {

    suspend fun getHomepage(): Homepage

    suspend fun getDeal(dealId: Int, categoryId: Int): Deal

    suspend fun searchDeals(searchText: String): List<Deal>

}