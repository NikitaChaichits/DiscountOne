package com.digeltech.appdiscountone.data.mapper

import com.digeltech.appdiscountone.data.model.AllDealsDto
import com.digeltech.appdiscountone.data.model.DealDto
import com.digeltech.appdiscountone.data.model.ItemDto
import com.digeltech.appdiscountone.domain.model.AllDeals
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.model.Item

class DealsMapper {

    fun mapDeals(data: List<DealDto>): List<Deal> = data.map {
        mapToDeal(it)
    }

    fun mapAllDeals(data: AllDealsDto): AllDeals = AllDeals(
        categories = data.categories.mapItems(),
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
            viewsClick = data.viewsClick,
            webLink = data.webLink,
        )
    }

    private fun List<ItemDto>.mapItems(): List<Item> {
        return map { Item(it.id, it.name) }
    }
}