package com.digeltech.discountone.ui.home.adapter

import com.digeltech.discountone.domain.model.CategoryWithSubcategories

class CategoryPaginator(
    private val categories: List<CategoryWithSubcategories>,
    private val pageSize: Int = 3
) {
    private var currentPage = 0

    fun hasNextPage(): Boolean {
        return (currentPage + 1) * pageSize < categories.size
    }

    fun getNextPage(): List<CategoryWithSubcategories> {
        val fromIndex = currentPage * pageSize
        val toIndex = minOf((currentPage + 1) * pageSize, categories.size)
        val nextPage = categories.subList(fromIndex, toIndex)
        currentPage++
        return nextPage
    }
}
