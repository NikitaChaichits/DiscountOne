package com.digeltech.discountone.domain.repository

import com.digeltech.discountone.domain.model.Category
import com.digeltech.discountone.domain.model.Item
import com.digeltech.discountone.domain.model.SubscriptionCategories

interface CategoriesRepository {

    suspend fun getAllCategories(): Result<List<Category>>

    suspend fun getCategoryShops(pageslug: String): Result<List<Item>>

    suspend fun getSubscriptionCategories(userId: String): Result<SubscriptionCategories>

    suspend fun updateSubscriptionCategories(
        userId: String,
        isEmailNotificationOn: Boolean?,
        unselectedNotificationCategories: String?
    ): Result<Unit>
}