package com.digeltech.appdiscountone.data.source.remote.api

import com.digeltech.appdiscountone.domain.model.Deal
import retrofit2.http.GET

interface ServerApi {

    @GET("SELECT * FROM wp_term_taxonomy")
    suspend fun getCouponsList(): List<Deal>

}