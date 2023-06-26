package com.digeltech.appdiscountone.data.mapper

import com.digeltech.appdiscountone.data.model.CategoryDto
import com.digeltech.appdiscountone.domain.model.Category

class CategoryMapper {

    fun map(data: List<CategoryDto>) = data.map { it.mapToCategory() }

    private fun CategoryDto.mapToCategory() = Category(
        id = id,
        name = name,
        slug = slug,
        countOfItems = countOfItems,
        icon = icon,
    )

}