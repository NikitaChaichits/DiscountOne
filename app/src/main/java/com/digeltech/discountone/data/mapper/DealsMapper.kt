package com.digeltech.discountone.data.mapper

import com.digeltech.discountone.data.model.*
import com.digeltech.discountone.domain.model.AllDeals
import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.model.Item
import com.digeltech.discountone.domain.model.ItemWithChild
import com.digeltech.discountone.ui.common.model.DealType

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
            id = data.id ?: 0,
            categoryId = data.categoryId ?: 0,
            title = data.title.toString(),
            description = data.description.toString(),
            imageUrl = data.imageUrl.toString(),
            imagesUrl = data.imagesUrl,
            bannerImageUrl = data.bannerImageUrl,
            shopName = data.shopName.toString(),
            shopSlug = data.shopSlug.toString(),
            shopImageUrl = data.shopImageUrl.toString(),
            oldPrice = data.oldPrice?.toInt() ?: 0,
            price = data.price?.toInt() ?: 0,
            promocode = data.promocode,
            shopLink = data.shopLink.toString(),
            rating = data.rating ?: 0,
            publishedDate = data.publishedDate.toString(),
            expirationDate = data.expirationDate,
            lastUpdateDate = data.lastUpdateDate,
            sale = data.sale,
            saleSize = data.saleSize ?: 0,
            viewsClick = data.viewsClick ?: 0,
            webLink = data.webLink,
            dealType = if (data.dealType == "coupons") DealType.COUPONS
            else DealType.DISCOUNTS,
            couponsTypeName = data.couponsTypeName,
            couponsTypeSlug = data.couponsTypeSlug,
            couponsCategory = data.couponsCategory
        )
    }

    fun mapOtherDeals(data: OtherDealsDto): List<Deal> = data.posts.map(::mapToDeal)

    fun mapTopCoupons(data: List<DealDto>): List<Deal> = data.map(::mapToDeal)

    fun mapBookmarks(data: List<DealDto>): List<Deal> = data.map {
        mapToBookmark(it)
    }

    private fun mapToBookmark(data: DealDto): Deal {
        return Deal(
            id = data.id ?: 0,
            categoryId = data.categoryId ?: 0,
            title = data.title.toString(),
            description = data.description.toString(),
            imageUrl = data.imageUrl.toString(),
            imagesUrl = data.imagesUrl,
            bannerImageUrl = data.bannerImageUrl,
            shopName = data.shopName.toString(),
            shopSlug = data.shopSlug.toString(),
            shopImageUrl = data.shopImageUrl.toString(),
            oldPrice = data.oldPrice?.toInt() ?: 0,
            price = data.price?.toInt() ?: 0,
            promocode = data.promocode,
            shopLink = data.shopLink.toString(),
            rating = data.rating ?: 0,
            publishedDate = data.publishedDate.toString(),
            expirationDate = data.expirationDate,
            lastUpdateDate = data.lastUpdateDate,
            sale = data.sale,
            saleSize = data.saleSize ?: 0,
            viewsClick = data.viewsClick ?: 0,
            webLink = data.webLink,
            isAddedToBookmark = true,
            dealType = if (data.dealType == "coupons") DealType.COUPONS
            else DealType.DISCOUNTS,
            couponsTypeName = data.couponsTypeName,
            couponsTypeSlug = data.couponsTypeSlug,
            couponsCategory = data.couponsCategory
        )
    }

    private fun List<ItemWithChildDto>.mapChildItems(): List<ItemWithChild> {
        return map { ItemWithChild(it.id, it.name, it.slug, it.taxonomy, it.child.mapItems()) }
    }

    private fun List<ItemDto>.mapItems(): List<Item> {
        return map { Item(it.id, it.name, it.slug, it.taxonomy) }
    }
}