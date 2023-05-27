package com.digeltech.appdiscountone.data.mapper

import com.digeltech.appdiscountone.data.model.BannerDto
import com.digeltech.appdiscountone.data.model.CategoryWithItemsDto
import com.digeltech.appdiscountone.data.model.DealDto
import com.digeltech.appdiscountone.data.model.HomepageDto
import com.digeltech.appdiscountone.domain.model.CategoryWithDeals
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.model.Homepage
import com.digeltech.appdiscountone.ui.home.adapter.Banner

class HomepageMapper {

    fun map(data: HomepageDto) = Homepage(
        listOfBanners = data.listOfBanners.map { it.mapBanner() },
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
            description = description,
            imageUrl = imageUrl.toString(),
            shopName = shopName.toString(),
            shopImageUrl = shopImageUrl.toString(),
            oldPrice = oldPrice,
            price = price,
            promocode = promocode,
            link = link.toString(),
            rating = rating,
            publishedDate = publishedDate,
            expirationDate = expirationDate,
            sale = sale,
            viewsClick = viewsClick
        )
    }

}