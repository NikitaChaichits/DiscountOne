package com.digeltech.discountone.domain.model

data class AllDeals(
    val categories: List<ItemWithChild>,
    val shops: List<Item>,
    val posts: List<Deal>,
)

data class ItemWithChild(
    val id: Int,
    val name: String,
    val slug: String,
    val taxonomy: String?,
    val child: List<Item>
)

data class Item(
    val id: Int,
    val name: String,
    val slug: String,
    val taxonomy: String?,
    var isParent: Boolean = false
)

fun List<Item>.getTaxonomyBySlug(slug: String): String? {
    return find { it.slug == slug }?.taxonomy
}