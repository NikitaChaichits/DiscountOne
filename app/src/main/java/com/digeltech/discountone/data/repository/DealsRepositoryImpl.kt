package com.digeltech.discountone.data.repository

import com.digeltech.discountone.data.mapper.DealsMapper
import com.digeltech.discountone.data.mapper.HomepageMapper
import com.digeltech.discountone.data.source.remote.api.ServerApi
import com.digeltech.discountone.domain.model.AllDeals
import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.model.Homepage
import com.digeltech.discountone.domain.repository.DealsRepository
import com.digeltech.discountone.ui.common.model.CategoryType
import com.digeltech.discountone.ui.common.model.SortBy
import com.digeltech.discountone.ui.common.model.Sorting
import com.digeltech.discountone.ui.home.KEY_HOMEPAGE_DATA
import com.digeltech.discountone.util.log
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DealsRepositoryImpl @Inject constructor(
    private val api: ServerApi,
) : DealsRepository {

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

    override suspend fun getDealsByCategoryAndShopId(page: String, categoryId: Int?, shopId: Int?): Result<List<Deal>> =
        withContext(Dispatchers.IO) {
            runCatching {
                DealsMapper().mapDeals(api.getSortingBestDeals(page, categoryId, shopId))
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

    override suspend fun getSimilarDealsByCategory(categoryName: String): List<Deal> = withContext(Dispatchers.IO) {
        DealsMapper().mapOtherDeals(api.getTopDeals(categoryName))
    }

    override suspend fun getSimilarDealsByShop(shopName: String): List<Deal> = withContext(Dispatchers.IO) {
        DealsMapper().mapOtherDeals(api.getOtherDeals(shopName))
    }

    override suspend fun getSortingDeals(
        page: String,
        categoryType: CategoryType,
        taxSlug: String,
        sorting: Sorting,
        sortBy: SortBy,
        catOrShopSlug: String?,
        priceFrom: Int?,
        priceTo: Int?
    ): Result<List<Deal>> = withContext(Dispatchers.IO) {
        runCatching {
            DealsMapper().mapDeals(
                api.getSortingDeals(
                    page = page,
                    categoryType = categoryType.type,
                    taxSlug = taxSlug,
                    sorting = sorting.name,
                    sortBy = sortBy.type,
                    catOrShopSlug = catOrShopSlug,
                    priceFrom = priceFrom,
                    priceTo = priceTo,
                )
            )
        }
    }

    override suspend fun getInitialDeals(categoryType: CategoryType, id: String): Result<List<Deal>> =
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