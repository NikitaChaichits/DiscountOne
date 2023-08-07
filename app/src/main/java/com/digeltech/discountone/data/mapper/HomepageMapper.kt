package com.digeltech.discountone.data.mapper

import com.digeltech.discountone.data.model.*
import com.digeltech.discountone.domain.model.CategoryWithDeals
import com.digeltech.discountone.domain.model.CategoryWithSubcategories
import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.model.Homepage
import com.digeltech.discountone.ui.home.adapter.Banner

class HomepageMapper {

    fun map(data: HomepageDto) = Homepage(
        listOfBanners = DealsMapper().mapDeals(data.listOfBanners),
//        soloBanner = data.soloBanner.first().mapBanner(),
        bestDeals = data.bestDeals.first().mapCategories("Best Deals"),
        categories = data.categories.map { it.mapCategories() }
    )

    private fun BannerDto.mapBanner() = Banner(
        urlImage = urlImage,
        dealId = dealId,
        categoryId = categoryId
    )

    private fun CategoryWithItemsDto.mapCategories(categoryName: String) = CategoryWithDeals(
        id = id,
        name = name,
        slug = slug.toString(),
        parentName = categoryName,
        items = items.map { it.mapToDeal(id) }
    )

    private fun CategoryWithSubcategoriesDto.mapCategories() = CategoryWithSubcategories(
        id = id,
        name = name,
        subcategories = subcategories.map { it.mapCategories(name) }
    )

    private fun DealDto.mapToDeal(categoryId: Int): Deal {
        return Deal(
            id = id,
            categoryId = categoryId,
            title = title,
            description = description.toString(),
            imageUrl = imageUrl.toString(),
            imagesUrl = imagesUrl,
            shopName = shopName.toString(),
            shopSlug = shopSlug.toString(),
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