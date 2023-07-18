package com.digeltech.discountone.data.mapper

import com.digeltech.discountone.data.model.CategoryDto
import com.digeltech.discountone.data.model.SubcategoryDto
import com.digeltech.discountone.domain.model.Category
import com.digeltech.discountone.domain.model.Subcategory

class CategoryMapper {

    fun map(data: List<CategoryDto>) = data.map { it.mapToCategory() }

    private fun CategoryDto.mapToCategory() = Category(
        id = id,
        name = name,
        slug = slug,
        countOfItems = countOfItems,
        icon = icon,
        subcategory = subcategory.map { it.mapToSubcategory() }
    )

    private fun SubcategoryDto.mapToSubcategory() = Subcategory(
        id = id,
        name = name,
        slug = slug,
        countOfItems = countOfItems,
        icon = icon,
    )

}