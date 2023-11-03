package com.digeltech.discountone.data.model

import com.google.gson.annotations.SerializedName

data class SubscriptionCategoriesDto(
    @SerializedName("email_notification")
    val emailNotification: Boolean?,
    @SerializedName("subscription_categories")
    val subscriptionCategories: List<SubscriptionCategoryDto>,
)

data class SubscriptionCategoryDto(
    @SerializedName("term_id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("slug")
    val slug: String?,
    @SerializedName("notification")
    val isNotificationOff: Boolean?,
    @SerializedName("image_url")
    var iconUrl: String?,
)