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
            link = data.link.toString(),
            rating = data.rating,
            publishedDate = data.publishedDate,
            expirationDate = data.expirationDate,
            sale = data.sale,
            viewsClick = data.viewsClick
        )
    }
}