package com.digeltech.discountone.data.mapper

import com.digeltech.discountone.data.model.BannerDto
import com.digeltech.discountone.data.model.CategoryWithItemsDto
import com.digeltech.discountone.data.model.DealDto
import com.digeltech.discountone.data.model.HomepageDto
import com.digeltech.discountone.domain.model.CategoryWithDeals
import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.model.Homepage
import com.digeltech.discountone.ui.home.adapter.Banner

class HomepageMapper {

    fun map(data: HomepageDto) = Homepage(
        listOfBanners = DealsMapper().mapDeals(data.listOfBanners),
        soloBanner = data.soloBanner.first().mapBanner(),
        categories = data.homeCategories.map { it.mapHomeCategories() }
    )

    private fun BannerDto.mapBanner() = Banner(
        urlImage = urlImage,
        dealId = dealId,
        categoryId = categoryId
    )

    private fun CategoryWithItemsDto.mapHomeCategories() = CategoryWithDeals(
        id = id,
        name = name,
        items = items.map { it.mapToDeal(id) }
    )

    private fun DealDto.mapToDeal(categoryId: Int): Deal {
        return Deal(
            id = id,
            categoryId = categoryId,
            title = title,
            description = description.toString(),
            imageUrl = imageUrl.toString(),
            shopName = shopName.toString(),
            shopImageUrl = shopImageUrl.toString(),
            oldPrice = oldPrice,
            price = price,
            promocode = promocode,
            shopLink = shopLink.toString(),
            rating = rating.toString(),
            publishedDate = publishedDate,
            expirationDate = expirationDate,
            sale = sale,
            viewsClick = viewsClick ?: 0,
            webLink = webLink
        )
    }

}