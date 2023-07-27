package com.digeltech.discountone.ui.home.adapter

import com.digeltech.discountone.domain.model.CategoryWithDeals

class CategoryPaginator(
    private val categories: List<CategoryWithDeals>,
    private val pageSize: Int = 10
) {
    private var currentPage = 0

    fun hasNextPage(): Boolean {
        return (currentPage + 1) * pageSize < categories.size
    }

    fun hasLastPage(): Boolean {
        return (currentPage) * pageSize < categories.size
    }

    fun getNextPage(): List<CategoryWithDeals> {
        val fromIndex = currentPage * pageSize
        val toIndex = minOf((currentPage + 1) * pageSize, categories.size)
        val nextPage = categories.subList(fromIndex, toIndex)
        currentPage++
        return nextPage
    }

    fun getLastPage(): List<CategoryWithDeals> {
        val fromIndex = currentPage * pageSize
        val toIndex = categories.size
        val nextPage = categories.subList(fromIndex, toIndex)
        currentPage++
        return nextPage
    }
}
