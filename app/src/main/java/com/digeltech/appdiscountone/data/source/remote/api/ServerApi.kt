package com.digeltech.appdiscountone.data.source.remote.api

import com.digeltech.appdiscountone.data.model.CategoryDto
import com.digeltech.appdiscountone.data.model.DealDto
import com.digeltech.appdiscountone.data.model.HomepageDto
import com.digeltech.appdiscountone.data.model.ShopDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ServerApi {

    @GET("/wp-json/theme/v1/shop")
    suspend fun getAllShops(): List<ShopDto>

    @GET("/wp-json/theme/v1/categories")
    suspend fun getAllCategories(): List<CategoryDto>

    @GET("/wp-json/theme/v1/homepage")
    suspend fun getHomepage(): HomepageDto

    @GET("/wp-json/theme/v1/promocodes/page=1&limit=1000")
    suspend fun getAllCoupons(): List<DealDto>

    @GET("/wp-json/theme/v1/products/page=1&limit=1000")
    suspend fun getAllDeals(): List<DealDto>

    @GET("/wp-json/theme/v1/products/id/{id}")
    suspend fun getDeal(@Path("id") id: String): DealDto

    @GET("/wp-json/theme/v1/list_categories?categories=categories&page=1&limit=1000")
    suspend fun getCategoryDeals(@Query("id") id: String): List<DealDto>

    @GET("/wp-json/theme/v1/list_categories?categories=categories-shops&page=1&limit=1000")
    suspend fun getShopDeals(@Query("id") id: String): List<DealDto>

    @GET("/wp-json/theme/v1/products/search/{searchText}")
    suspend fun searchDeals(@Path("searchText") id: String): List<DealDto>

}