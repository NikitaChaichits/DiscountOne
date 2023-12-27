package com.digeltech.discountone.data.repository

import com.digeltech.discountone.data.mapper.CouponsMapper
import com.digeltech.discountone.data.source.remote.api.CouponsApi
import com.digeltech.discountone.domain.model.Coupons
import com.digeltech.discountone.domain.repository.CouponsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class CouponsRepositoryImpl @Inject constructor(
    private val api: CouponsApi,
) : CouponsRepository {

    override suspend fun getCoupons(): Result<Coupons> = withContext(Dispatchers.IO) {
        runCatching {
            CouponsMapper().mapCoupons(api.getCoupons())
        }
    }

}