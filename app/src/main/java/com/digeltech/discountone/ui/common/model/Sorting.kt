package com.digeltech.discountone.ui.common.model

enum class Sorting {
    ASC, DESC
}

//enum class SortBy(val type: String) {
//    PRICE("price"),
//    SALE_SIZE("sale_size"),
//    VIEWS_CLICK("views_click"),
//    DATE("date"),
//}

enum class SortBy(val type: String) {
    NEW_DEALS("new_deals"),
    MOST_POPULAR("most_popular"),
    HIGH_DISCOUNT("high_discount"),
    PRICE_ASC("price_asc"),
    PRICE_DESC("price_desc"),
}

enum class CategoryType(val type: String) {
    CATEGORY("categories"),
    SHOP("categories-shops"),
    COUPONS("categories-coupons")
}

enum class DealType(val type: String) {
    ALL("null"),
    DISCOUNTS("products"),
    COUPONS("coupons")
}