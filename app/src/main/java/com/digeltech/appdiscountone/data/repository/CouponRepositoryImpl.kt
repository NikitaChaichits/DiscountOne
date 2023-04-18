package com.digeltech.appdiscountone.data.repository

import com.digeltech.appdiscountone.data.source.remote.DatabaseConnection
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.repository.CouponRepository
import com.digeltech.appdiscountone.domain.result.Result
import javax.inject.Inject

class CouponRepositoryImpl @Inject constructor(
    private val databaseConnection: DatabaseConnection,
) : CouponRepository {

    override suspend fun getCouponsList(): Result<List<Deal>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCouponById(id: String): Result<Deal> {
        TODO("Not yet implemented")
    }

}