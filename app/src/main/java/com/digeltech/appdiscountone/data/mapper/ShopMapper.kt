package com.digeltech.appdiscountone.data.mapper

import com.digeltech.appdiscountone.data.model.ShopDto
import com.digeltech.appdiscountone.domain.model.Shop

class ShopMapper {

    fun map(data: List<ShopDto>) = data.map { it.mapToShop() }

    private fun ShopDto.mapToShop() = Shop(
        id = id,
        name = name,
        slug = slug,
        countOfItems = countOfItems,
        icon = icon,
        popular = popular == "1"
    )

}