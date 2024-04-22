package com.digeltech.discountone.data.source.remote.api

import com.digeltech.discountone.data.model.PriceChangeDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ParserApi {

    @GET("/products/{id}/prices?apiKey=gn3uv6ldg9i8xtc4osesnt")
    suspend fun getPriceChangeOfDeal(@Path("id") id: String): PriceChangeDto
}