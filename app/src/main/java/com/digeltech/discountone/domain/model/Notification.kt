package com.digeltech.discountone.domain.model

data class Notification(
    val title: String,
    val text: String,
    val date: String,
    val data: Map<String, String>,
    var isRead: Boolean = false
)
