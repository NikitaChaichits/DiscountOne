package com.digeltech.discountone.data.source.remote.api

import com.digeltech.discountone.data.model.*
import retrofit2.http.GET
import retrofit2.http.POST
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

    @GET("/wp-json/theme/v1/products")
    suspend fun getAllDeals(
        @Query("page") page: String,
        @Query("limit") limit: String
    ): List<DealDto>

    @GET("/wp-json/theme/v1/best_deals")
    suspend fun getBestDeals(): AllDealsDto

    @GET("/wp-json/theme/v1/products/id/{id}")
    suspend fun getDeal(@Path("id") id: String): DealDto

    @GET("/wp-json/theme/v1/products/search/{searchText}")
    suspend fun searchDeals(@Path("searchText") id: String): List<DealDto>

    @GET("/wp-json/theme/v1/views")
    suspend fun updateViewsClick(@Query("id") id: String)

    @GET("/wp-json/theme/v1/top_deals")
    suspend fun getTopDeals(@Query("cat") categorySlugName: String): OtherDealsDto

    @GET("/wp-json/theme/v1/other_deals")
    suspend fun getOtherDeals(@Query("shop") shopSlugName: String): OtherDealsDto

    @GET("/wp-json/theme/v1/list_categories_start_page")
    suspend fun getInitialDeals(
        @Query("categories") categoryType: String,
        @Query("id") id: String
    ): List<DealDto>

    @GET("/wp-json/theme/v1/filter_cat")
    suspend fun getSortingDeals(
        @Query("page") page: String,
        @Query("cat") categoryType: String,
        @Query("tax_slug") taxSlug: String,
        @Query("sort") sorting: String,
        @Query("sortBy") sortBy: String,
        @Query("cat_or_shop") catOrShopSlug: String?,
        @Query("priceFrom") priceFrom: Int?,
        @Query("priceTo") priceTo: Int?
    ): List<DealDto>

    @GET("/wp-json/theme/v1/list_categories?categories=categories&limit=100")
    suspend fun getSortingBestDeals(
        @Query("page") page: String,
        @Query("id_category") idCategory: Int?,
        @Query("id_shop") idShop: Int?
    ): List<DealDto>

    @GET("/wp-json/theme/v1/cat_and_chop_filter?category=categories")
    suspend fun getCategoryStores(@Query("pageslug") pageslug: String): List<CategoryShopDto>

    @GET("/wp-json/theme/v1/cat_and_chop_filter?category=categories-shops")
    suspend fun getShopCategories(@Query("pageslug") pageslug: String): List<CategoryShopDto>

    @GET("/wp-json/theme/v1/users/save_coupons")
    suspend fun getFavoritesDeals(@Query("id") userId: String): List<DealDto>

    @GET("/wp-json/theme/v1/users/upload_save_coupons")
    suspend fun saveOrDeleteFavoriteDeal(
        @Query("id") userId: String,
        @Query("id_coupons") dealId: String
    )

    @GET("/wp-json/theme/v1/subscription-preferences")
    suspend fun getSubscriptionCategories(
        @Query("id") userId: String
    ): SubscriptionCategoriesDto

    @POST("/wp-json/theme/v1/subscription-preferences_push")
    suspend fun updateSubscriptionCategories(
        @Query("id") userId: String,
        @Query("notification") notification: Boolean?,
        @Query("notification_cat") notificationCategoriesUnsubscribe: String?,
    )
}