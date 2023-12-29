package com.digeltech.discountone.data.repository

import com.digeltech.discountone.data.mapper.DealsMapper
import com.digeltech.discountone.data.mapper.HomepageMapper
import com.digeltech.discountone.data.source.remote.api.DiscountApi
import com.digeltech.discountone.data.source.remote.api.ServerApi
import com.digeltech.discountone.domain.model.AllDeals
import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.model.Homepage
import com.digeltech.discountone.domain.model.Shop
import com.digeltech.discountone.domain.repository.DealsRepository
import com.digeltech.discountone.ui.common.KEY_SHOPS
import com.digeltech.discountone.ui.common.model.DealType
import com.digeltech.discountone.ui.common.model.SortBy
import com.digeltech.discountone.ui.common.model.Taxonomy
import com.digeltech.discountone.ui.home.KEY_HOMEPAGE_DATA
import com.digeltech.discountone.util.log
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DealsRepositoryImpl @Inject constructor(
    private val api: ServerApi,
    private val discountApi: DiscountApi,
) : DealsRepository {

    override suspend fun getDiscounts(): Result<AllDeals> = withContext(Dispatchers.IO) {
        runCatching {
            DealsMapper().mapAllDeals(discountApi.getDiscounts())
        }
    }

    override suspend fun getBestDeals(): Result<AllDeals> = withContext(Dispatchers.IO) {
        runCatching {
            DealsMapper().mapAllDeals(api.getBestDeals())
        }
    }

    override suspend fun getAllDeals(page: String, limit: String): Result<List<Deal>> = withContext(Dispatchers.IO) {
        runCatching {
            DealsMapper().mapDeals(api.getAllDeals(page, limit))
        }
    }

    override suspend fun getAllCoupons(): Result<List<Deal>> = withContext(Dispatchers.IO) {
        runCatching {
            DealsMapper().mapDeals(api.getAllCoupons())
        }
    }

    override suspend fun getDealById(dealId: Int): Result<Deal> = withContext(Dispatchers.IO) {
        runCatching {
            DealsMapper().mapToDeal(api.getDeal(dealId.toString()))
        }
    }

    override suspend fun getHomepage(): Result<Homepage> = withContext(Dispatchers.IO) {
        runCatching {
            if (Hawk.contains(KEY_HOMEPAGE_DATA)) {
                Hawk.get(KEY_HOMEPAGE_DATA)
            } else {
                HomepageMapper().map(api.getHomepage()).also {
                    Hawk.put(KEY_HOMEPAGE_DATA, it)
                    val cacheShops = it.shops.map { shop ->
                        Shop(
                            id = shop.id,
                            name = shop.name,
                            slug = shop.slug,
                            countOfItems = 0,
                            icon = shop.icon,
                            popular = false
                        )
                    }
                    Hawk.put(KEY_SHOPS, cacheShops)
                }
            }
        }
    }

    override suspend fun searchDeals(searchText: String): List<Deal> = withContext(Dispatchers.IO) {
        DealsMapper().mapDeals(api.searchDeals(searchText))
    }

    override suspend fun updateDealViewsClick(id: String) = withContext(Dispatchers.IO) {
        api.updateViewsClick(id).also {
            log("api.updateViewsClick($id)")
        }
    }

    override suspend fun getSimilarDealsByCategory(categoryName: String): Result<List<Deal>> =
        withContext(Dispatchers.IO) {
            runCatching {
                DealsMapper().mapOtherDeals(api.getTopDeals(categoryName))
            }
        }

    override suspend fun getSimilarCouponsByCategory(): Result<List<Deal>> =
        withContext(Dispatchers.IO) {
            runCatching {
                DealsMapper().mapOtherDeals(api.getTopCoupons())
            }
        }

    override suspend fun getSimilarDealsByShop(shopName: String): Result<List<Deal>> = withContext(Dispatchers.IO) {
        runCatching {
            DealsMapper().mapOtherDeals(api.getOtherDeals(shopName))
        }
    }

    override suspend fun getSimilarCouponsByShop(shopName: String): Result<List<Deal>> = withContext(Dispatchers.IO) {
        runCatching {
            DealsMapper().mapOtherDeals(api.getOtherCoupons(shopName))
        }
    }

    override suspend fun getSortingDeals(
        page: String,
        dealType: DealType?,
        sortBy: SortBy?,
        categorySlug: String?,
        shopSlug: String?,
        taxonomy: String?,
    ): Result<List<Deal>> = withContext(Dispatchers.IO) {
        runCatching {
            DealsMapper().mapDeals(
                api.getSortingDeals(
                    page = page,
                    sortBy = sortBy?.type,
                    categorySlug = categorySlug,
                    shopSlug = shopSlug,
                    dealType = dealType?.type,
                    taxonomy = taxonomy
                )
            )
        }
    }

    override suspend fun getInitialDeals(categoryType: Taxonomy, id: String): Result<List<Deal>> =
        withContext(Dispatchers.IO) {
            runCatching {
                DealsMapper().mapDeals(
                    api.getInitialDeals(
                        categoryType = categoryType.type,
                        id = id
                    )
                )
            }
        }

    override suspend fun getBookmarksDeals(userId: String): Result<List<Deal>> = withContext(Dispatchers.IO) {
        runCatching {
            DealsMapper().mapBookmarks(api.getFavoritesDeals(userId = userId))
        }
    }

    override suspend fun updateBookmark(userId: String, dealId: String): Unit = withContext(Dispatchers.IO) {
        runCatching {
            api.saveOrDeleteBookmark(userId = userId, dealId = dealId)
        }
    }

}