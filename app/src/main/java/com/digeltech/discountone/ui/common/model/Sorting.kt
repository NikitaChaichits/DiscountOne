package com.digeltech.discountone.ui.common.model

enum class Sorting {
    ASC, DESC
}

enum class SortBy(val type: String) {
    PRICE("price"),
    SALE_SIZE("sale_size"),
    VIEWS_CLICK("views_click"),
    DATE("date"),
}

enum class CategoryType(val type: String) {
    CATEGORY("categories"),
    SHOP("categories-shops")
}