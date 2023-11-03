package com.digeltech.discountone.data.mapper

import com.digeltech.discountone.data.model.SubscriptionCategoriesDto
import com.digeltech.discountone.data.model.SubscriptionCategoryDto
import com.digeltech.discountone.domain.model.SubscriptionCategories
import com.digeltech.discountone.domain.model.SubscriptionCategory

class SubscriptionMapper {

    fun map(data: SubscriptionCategoriesDto) = SubscriptionCategories(
        emailNotification = data.emailNotification ?: true,
        subscriptionCategories = data.subscriptionCategories.map { it.map() },
    )

    private fun SubscriptionCategoryDto.map() = SubscriptionCategory(
        id = id ?: 0,
        name = name ?: "",
        slug = slug ?: "",
        isNotificationOff = isNotificationOff ?: false,
        iconUrl = iconUrl ?: "",
    )

}