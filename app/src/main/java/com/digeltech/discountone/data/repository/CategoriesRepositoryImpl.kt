package com.digeltech.discountone.data.repository

import com.digeltech.discountone.data.mapper.CategoryMapper
import com.digeltech.discountone.data.mapper.SubscriptionMapper
import com.digeltech.discountone.data.source.remote.api.ServerApi
import com.digeltech.discountone.domain.model.Category
import com.digeltech.discountone.domain.model.CategoryShopFilterItem
import com.digeltech.discountone.domain.model.SubscriptionCategories
import com.digeltech.discountone.domain.repository.CategoriesRepository
import com.digeltech.discountone.ui.common.KEY_CATEGORIES
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val api: ServerApi,
) : CategoriesRepository {

    override suspend fun getAllCategories(): Result<List<Category>> = withContext(Dispatchers.IO) {
        runCatching {
            CategoryMapper().map(api.getAllCategories()).also {
                Hawk.put(KEY_CATEGORIES, it)
            }
        }
    }

    override suspend fun getCategoryShops(pageslug: String): Result<List<CategoryShopFilterItem>> =
        withContext(Dispatchers.IO) {
            runCatching {
                api.getCategoryStores(pageslug).map {
                    CategoryShopFilterItem(it.name, it.slug)
                }
            }
        }

    override suspend fun getSubscriptionCategories(userId: String): Result<SubscriptionCategories> =
        withContext(Dispatchers.IO) {
            runCatching {
                SubscriptionMapper().map(api.getSubscriptionCategories(userId))
            }
        }

    override suspend fun updateSubscriptionCategories(
        userId: String,
        isEmailNotificationOn: Boolean?,
        unselectedNotificationCategories: String?
    ) = withContext(Dispatchers.IO) {
        runCatching {
            api.updateSubscriptionCategories(
                userId = userId,
                notification = isEmailNotificationOn,
                notificationCategoriesUnsubscribe = unselectedNotificationCategories
            )
        }
    }
}