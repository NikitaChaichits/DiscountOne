package com.digeltech.appdiscountone.domain.repository

import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.result.Result

interface CouponRepository {
    suspend fun getCouponsList(): Result<List<Deal>>

    suspend fun getCouponById(id: String): Result<Deal>
}