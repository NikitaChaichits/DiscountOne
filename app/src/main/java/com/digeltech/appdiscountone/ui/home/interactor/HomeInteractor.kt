package com.digeltech.appdiscountone.ui.home.interactor

import com.digeltech.appdiscountone.domain.model.CategoryWithDeals
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.ui.home.adapter.Banner

interface HomeInteractor {

    suspend fun getBanners(): List<Banner>

    suspend fun getInitCategories(): List<CategoryWithDeals>

    suspend fun getAllCategories(): List<CategoryWithDeals>

    suspend fun getDeal(dealId: Int, categoryId: Int): Deal

}