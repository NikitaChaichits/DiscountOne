package com.digeltech.appdiscountone.domain.repository

import com.digeltech.appdiscountone.domain.model.Shop

interface ShopsRepository {

    suspend fun getAllShops(): Result<List<Shop>>

}