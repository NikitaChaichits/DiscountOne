package com.digeltech.discountone.data.mapper

import com.digeltech.discountone.data.model.CouponsDto
import com.digeltech.discountone.data.model.ItemDto
import com.digeltech.discountone.data.model.ItemWithChildDto
import com.digeltech.discountone.domain.model.Coupons
import com.digeltech.discountone.domain.model.Item
import com.digeltech.discountone.domain.model.ItemWithChild

class CouponsMapper {

    fun mapCoupons(data: CouponsDto): Coupons = Coupons(
        categories = data.categories.mapChildItems(),
        shops = data.shops.mapItems(),
        coupons = data.coupons.map(DealsMapper()::mapToDeal)
    )

    private fun List<ItemWithChildDto>.mapChildItems(): List<ItemWithChild> {
        return map { ItemWithChild(it.id, it.name, it.slug, it.taxonomy, it.child.mapItems()) }
    }

    private fun List<ItemDto>.mapItems(): List<Item> {
        return map { Item(it.id, it.name, it.slug, it.taxonomy) }
    }
}