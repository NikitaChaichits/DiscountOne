package com.digeltech.appdiscountone.domain.model

data class AllDeals(
    val categories: List<Item>,
    val shops: List<Item>,
    val posts: List<Deal>,
)

data class Item(
    val id: Int,
    val name: String,
)
