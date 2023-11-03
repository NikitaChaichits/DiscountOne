package com.digeltech.discountone.domain.model

data class SubscriptionCategories(
    val emailNotification: Boolean,
    val subscriptionCategories: List<SubscriptionCategory>,
)

data class SubscriptionCategory(
    val id: Int,
    val name: String,
    val slug: String,
    val isNotificationOff: Boolean,
    var iconUrl: String,
)