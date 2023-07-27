package com.digeltech.discountone.data.mapper

import com.digeltech.discountone.data.model.*
import com.digeltech.discountone.domain.model.AllDeals
import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.model.Item
import com.digeltech.discountone.domain.model.ItemWithChild

class DealsMapper {

    fun mapDeals(data: List<DealDto>): List<Deal> = data.map {
        mapToDeal(it)
    }

    fun mapAllDeals(data: AllDealsDto): AllDeals = AllDeals(
        categories = data.categories.mapChildItems(),
        shops = data.shops.mapItems(),
        posts = data.posts.map(::mapToDeal)
    )

    fun mapToDeal(data: DealDto): Deal {
        return Deal(
            id = data.id,
            categoryId = data.categoryId,
            title = data.title,
            description = data.description.toString(),
            imageUrl = data.imageUrl.toString(),
            imagesUrl = data.imagesUrl,
            bannerImageUrl = data.bannerImageUrl,
            shopName = data.shopName.toString(),
            shopImageUrl = data.shopImageUrl.toString(),
            oldPrice = data.oldPrice,
            price = data.price,
            promocode = data.promocode,
            shopLink = data.shopLink.toString(),
            rating = data.rating.toString(),
            publishedDate = data.publishedDate,
            expirationDate = data.expirationDate,
            sale = data.sale,
            saleSize = data.saleSize ?: 0,
            viewsClick = data.viewsClick ?: 0,
            webLink = data.webLink,
        )
    }

    fun mapOtherDeals(data: OtherDealsDto): List<Deal> = data.posts.map(::mapToDeal)

    private fun List<ItemWithChildDto>.mapChildItems(): List<ItemWithChild> {
        return map { ItemWithChild(it.id, it.name, it.child.mapItems()) }
    }

    private fun List<ItemDto>.mapItems(): List<Item> {
        return map { Item(it.id, it.name) }
    }
}