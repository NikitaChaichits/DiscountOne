package com.digeltech.appdiscountone.data.mapper

import com.digeltech.appdiscountone.data.model.DealDto
import com.digeltech.appdiscountone.domain.model.Deal

class DealsMapper {

    fun mapAllDeal(data: List<DealDto>): List<Deal> = data.map {
        mapToDeal(it)
    }

    fun mapToDeal(data: DealDto): Deal {
        return Deal(
            id = data.id,
            categoryId = data.categoryId,
            title = data.title,
            description = data.description,
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
}