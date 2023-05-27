package com.digeltech.appdiscountone.domain.model

data class Shop(
    val id: Int,
    val name: String,
    val countOfItems: Int,
    var icon: String?,
    val popular: Boolean,
)
