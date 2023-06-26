package com.digeltech.appdiscountone.domain.model

data class AllDeals(
    val categories: List<ItemWithChild>,
    val shops: List<Item>,
    val posts: List<Deal>,
)

data class ItemWithChild(
    val id: Int,
    val name: String,
    val child: List<Item>
)

data class Item(
    val id: Int,
    val name: String,
)
