package com.digeltech.discountone.data.source.remote.api

import com.digeltech.discountone.data.model.CouponsDto
import retrofit2.http.GET

interface CouponsApi {

    @GET("/wp-json/theme/v1/coupons/page-coupons")
    suspend fun getCoupons(): CouponsDto

}