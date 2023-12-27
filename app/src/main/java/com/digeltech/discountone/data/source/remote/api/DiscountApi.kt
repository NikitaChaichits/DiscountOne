package com.digeltech.discountone.data.source.remote.api

import com.digeltech.discountone.data.model.AllDealsDto
import retrofit2.http.GET

interface DiscountApi {

    @GET("/wp-json/theme/v1/discount")
    suspend fun getDiscounts(): AllDealsDto

}