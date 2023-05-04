package com.digeltech.appdiscountone.domain.repository

import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.ui.home.adapter.Banner

interface DealsRepository {
    suspend fun getDealsById(id: Int): List<Deal>

    suspend fun getSimilarDeals(dealId: Int, categoryId: Int): List<Deal>

    suspend fun getDealById(dealId: Int, categoryId: Int): Deal

    suspend fun getBanners(): List<Banner>
}