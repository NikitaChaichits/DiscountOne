package com.digeltech.discountone.domain.repository

import com.digeltech.discountone.domain.model.Coupons

interface CouponsRepository {

    suspend fun getCoupons(): Result<Coupons>

}