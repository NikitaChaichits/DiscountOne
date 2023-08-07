package com.digeltech.discountone.data.source.remote.api

import com.digeltech.discountone.data.model.*
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

    @GET("/wp-json/theme/v1/best_deals")
    suspend fun getBestDeals(): AllDealsDto

    @GET("/wp-json/theme/v1/products/id/{id}")
    suspend fun getDeal(@Path("id") id: String): DealDto

    @GET("/wp-json/theme/v1/list_categories?categories=categories&page=1&limit=1000")
    suspend fun getCategoryDeals(@Query("id") id: String): List<DealDto>

    @GET("/wp-json/theme/v1/list_categories?categories=categories-shops&page=1&limit=1000")
    suspend fun getShopDeals(@Query("id") id: String): List<DealDto>

    @GET("/wp-json/theme/v1/products/search/{searchText}")
    suspend fun searchDeals(@Path("searchText") id: String): List<DealDto>

    @GET("/wp-json/theme/v1/views")
    suspend fun updateViewsClick(@Query("id") id: String)

    @GET("/wp-json/theme/v1/top_deals")
    suspend fun getTopDeals(@Query("cat") categorySlugName: String): OtherDealsDto

    @GET("/wp-json/theme/v1/other_deals")
    suspend fun getOtherDeals(@Query("shop") shopSlugName: String): OtherDealsDto

    @GET("/wp-json/theme/v1/filter_cat")
    suspend fun getSortingDeals(
        @Query("page") page: String,
        @Query("cat") categoryType: String,
        @Query("tax_slug") taxSlug: String,
        @Query("sort") sorting: String,
        @Query("sortBy") sortBy: String,
        @Query("priceFrom") priceFrom: Int?,
        @Query("priceTo") priceTo: Int?,
        @Query("discountFrom") discountFrom: Int?,
        @Query("discountTo") discountTo: Int?
    ): List<DealDto>

}