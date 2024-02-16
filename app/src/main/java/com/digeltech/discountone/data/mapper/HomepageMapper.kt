package com.digeltech.discountone.data.mapper

import com.digeltech.discountone.data.model.CategoryWithItemsDto
import com.digeltech.discountone.data.model.DealDto
import com.digeltech.discountone.data.model.HomeShopDto
import com.digeltech.discountone.data.model.HomepageDto
import com.digeltech.discountone.domain.model.CategoryWithDeals
import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.model.HomeShop
import com.digeltech.discountone.domain.model.Homepage
import com.digeltech.discountone.ui.common.model.DealType

class HomepageMapper {

    fun map(data: HomepageDto) = Homepage(
        listOfBanners = DealsMapper().mapDeals(data.listOfBanners),
        discounts = data.discounts.first().mapCategories(),
        coupons = data.coupons.first().mapCategories(),
        shops = data.shops.map { it.mapToHomeShop() },
        categories = data.categories.map { it.mapCategories() }
    )

    private fun CategoryWithItemsDto.mapCategories() = CategoryWithDeals(
        id = id,
        name = name.toString(),
        slug = slug.toString(),
        items = items.map { it.mapToDeal(id) }
    )

    private fun DealDto.mapToDeal(categoryId: Int): Deal {
        return Deal(
            id = id ?: 0,
            categoryId = categoryId,
            title = title.toString(),
            description = description.toString(),
            imageUrl = imageUrl.toString(),
            imagesUrl = imagesUrl,
            shopName = shopName.toString(),
            shopSlug = shopSlug.toString(),
            shopImageUrl = shopImageUrl.toString(),
            oldPrice = oldPrice?.toInt() ?: 0,
            price = price?.toInt() ?: 0,
            promocode = promocode,
            shopLink = shopLink.toString(),
            rating = rating ?: 0,
            publishedDate = publishedDate.toString(),
            expirationDate = expirationDate,
            lastUpdateDate = lastUpdateDate,
            sale = sale,
            saleSize = saleSize ?: 0,
            viewsClick = viewsClick ?: 0,
            webLink = webLink,
            dealType = if (dealType == DealType.COUPONS.type) DealType.COUPONS
            else DealType.DISCOUNTS,
            couponsTypeName = couponsTypeName,
            couponsTypeSlug = couponsTypeSlug,
            couponsCategory = couponsCategory
        )
    }

    private fun HomeShopDto.mapToHomeShop(): HomeShop {
        return HomeShop(
            id = id ?: 0,
            name = name.toString(),
            icon = icon,
            slug = slug.toString()
        )
    }

}