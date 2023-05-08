package com.digeltech.appdiscountone.domain.repository

import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.ui.home.adapter.Banner

interface DealsRepository {
    suspend fun getAllDeals(limit: Int, offset: Int): List<Deal>

    suspend fun getAllCoupons(limit: Int, offset: Int): List<Deal>

    suspend fun getDealsByCategoryId(categoryId: Int, limit: Int, offset: Int): List<Deal>

    suspend fun getDealById(dealId: Int, categoryId: Int): Deal

    suspend fun getBanners(): List<Banner>
}