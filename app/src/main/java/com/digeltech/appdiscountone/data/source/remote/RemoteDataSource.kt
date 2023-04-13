package com.digeltech.appdiscountone.data.source.remote

import com.digeltech.appdiscountone.data.source.remote.api.ServerApi
import com.digeltech.appdiscountone.data.util.safeApiCall
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.result.Result
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val api: ServerApi) {

    suspend fun getCouponsList(): Result<List<Deal>> {
        return safeApiCall { api.getCouponsList() }
    }

}