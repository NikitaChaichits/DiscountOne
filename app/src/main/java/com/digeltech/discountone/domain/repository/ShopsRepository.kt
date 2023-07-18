package com.digeltech.discountone.domain.repository

import com.digeltech.discountone.domain.model.Shop

interface ShopsRepository {

    suspend fun getAllShops(): Result<List<Shop>>

}