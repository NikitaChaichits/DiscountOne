package com.digeltech.discountone.common.base

import androidx.lifecycle.MutableLiveData
import com.digeltech.discountone.domain.model.Item
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.ui.common.model.DealType
import com.digeltech.discountone.ui.common.model.SortBy
import com.digeltech.discountone.ui.common.model.Taxonomy

/**
 * Startup products loading 100 items, then additional loading by 50 items,
 * so current page for additional loading is 3
 */
const val CURRENT_PAGE_ON_START = 3

open class BaseFilteringViewModel : BaseViewModel() {


    val filteringError = MutableLiveData<String?>()

    val deals = MutableLiveData<List<DealParcelable>>()
    val filteringCategories = MutableLiveData<List<Item>>()
    val filteringShops = MutableLiveData<List<Item>>()

    var currentDealTypeSpinnerPosition = 0
    var currentSortBySpinnerPosition = 0
    var currentCategorySpinnerPosition = 0
    var currentShopSpinnerPosition = 0
    var currentPage = CURRENT_PAGE_ON_START
    var dealType = DealType.ALL
    var sortBy = SortBy.NEW_DEALS
    var categorySlug = ""
    var shopSlug = ""
    var isNeedMoreLoading = true

    init {
        clearReusableData()
    }

    private fun clearReusableData() {
        filteringJob?.cancel()
        currentDealTypeSpinnerPosition = 0
        currentSortBySpinnerPosition = 0
        currentCategorySpinnerPosition = 0
        currentShopSpinnerPosition = 0
        currentPage = CURRENT_PAGE_ON_START
        dealType = DealType.ALL
        sortBy = SortBy.NEW_DEALS
        categorySlug = ""
        shopSlug = ""
        isNeedMoreLoading = true
    }

    fun sortingByType(spinnerPosition: Int, callback: () -> Unit) {
        currentSortBySpinnerPosition = spinnerPosition
        when (spinnerPosition) {
            0 -> {
                sortBy = SortBy.NEW_DEALS
            }
            1 -> {
                sortBy = SortBy.MOST_POPULAR
            }
            2 -> {
                sortBy = SortBy.HIGH_DISCOUNT
            }
            3 -> {
                sortBy = SortBy.PRICE_ASC
            }
            4 -> {
                sortBy = SortBy.PRICE_DESC
            }
        }
        currentPage = CURRENT_PAGE_ON_START
        isNeedMoreLoading = true
        callback()
    }

    fun sortingByCategory(spinnerPosition: Int, callback: () -> Unit) {
        currentCategorySpinnerPosition = spinnerPosition
        when (dealType) {
            DealType.ALL -> {
                categorySlug = if (spinnerPosition == 0) {
                    ""
                } else {
                    filteringCategories.value?.get(spinnerPosition - 1)?.slug ?: ""
                }
            }
            DealType.COUPONS -> {
                categorySlug = if (spinnerPosition == 0) {
                    ""
                } else {
                    filteringCategories.value
                        ?.filter { it.taxonomy == Taxonomy.COUPONS.type }
                        ?.get(spinnerPosition - 1)?.slug ?: ""
                }
            }
            DealType.DISCOUNTS -> {
                categorySlug = if (spinnerPosition == 0) {
                    ""
                } else {
                    filteringCategories.value
                        ?.filter { it.taxonomy != Taxonomy.COUPONS.type }
                        ?.get(spinnerPosition - 1)?.slug ?: ""
                }
            }
        }

        currentPage = CURRENT_PAGE_ON_START
        isNeedMoreLoading = true
        callback()
    }

    fun sortingByShop(spinnerPosition: Int, callback: () -> Unit) {
        currentShopSpinnerPosition = spinnerPosition
        shopSlug = if (spinnerPosition == 0) {
            ""
        } else {
            filteringShops.value?.get(spinnerPosition - 1)?.slug ?: ""
        }
        currentPage = CURRENT_PAGE_ON_START
        isNeedMoreLoading = true
        callback()
    }

    fun sortingByDealType(spinnerPosition: Int, callback: () -> Unit) {
        currentDealTypeSpinnerPosition = spinnerPosition
        dealType = when (spinnerPosition) {
            0 -> {
                DealType.ALL
            }
            1 -> {
                DealType.DISCOUNTS
            }
            2 -> {
                DealType.COUPONS
            }
            else -> {
                DealType.ALL
            }
        }
        currentPage = CURRENT_PAGE_ON_START
        isNeedMoreLoading = true
        callback()
    }
}