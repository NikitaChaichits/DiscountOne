package com.digeltech.discountone.ui.categories.categoryandshop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.repository.DealsRepository
import com.digeltech.discountone.ui.common.SEARCH_DELAY
import com.digeltech.discountone.ui.common.model.*
import com.digeltech.discountone.util.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryAndShopViewModel @Inject constructor(
    private val dealsRepository: DealsRepository,
) : BaseViewModel() {

    private val _deals: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val deals: LiveData<List<DealParcelable>> = _deals

    private var currentPage = 1
    private var currentCategoryType: CategoryType = CategoryType.SHOP
    private lateinit var taxSlug: String
    private var currentSortByPosition = 0
    private var priceFrom = 0
    private var priceTo = 0
    private var discountFrom = 0
    private var discountTo = 0
    private var sorting = Sorting.DESC
    private var sortBy = SortBy.DATE

    fun initDeals(id: Int, slug: String, isFromCategory: Boolean) {
        if (isFromCategory) currentCategoryType = CategoryType.CATEGORY
        taxSlug = slug

//        viewModelScope.launchWithLoading {
//            if (isFromCategory) {
//                dealsRepository.getDealsByCategoryId(id)
//                    .onSuccess { deals ->
//                        _deals.postValue(deals.toParcelableList().sortedByDescending { it.id })
//                    }
//                    .onFailure {
//                        log(it.toString())
//                        error.postValue(it.toString())
//                    }
//            } else {
//                dealsRepository.getDealsByShopId(id)
//                    .onSuccess { deals ->
//                        _deals.postValue(deals.toParcelableList().sortedByDescending { it.id })
//                    }
//                    .onFailure {
//                        log(it.toString())
//                        error.postValue(it.toString())
//                    }
//            }
//
//        }
    }

    fun searchDeals(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)

            val deals = dealsRepository.searchDeals(searchText)
            searchResult.value = deals.toParcelableList()
        }
    }

    fun updateDealViewsClick(id: String) {
        viewModelScope.launch {
            dealsRepository.updateDealViewsClick(id)
        }
    }

    fun sortingByType(spinnerPosition: Int) {
        currentSortByPosition = spinnerPosition
        when (spinnerPosition) {
            0 -> {
                sortBy = SortBy.DATE
                sorting = Sorting.DESC
            }
            1 -> {
                sortBy = SortBy.VIEWS_CLICK
                sorting = Sorting.DESC
            }
            2 -> {
                sortBy = SortBy.SALE_SIZE
                sorting = Sorting.DESC
            }
            3 -> {
                sortBy = SortBy.PRICE
                sorting = Sorting.ASC
            }
            4 -> {
                sortBy = SortBy.PRICE
                sorting = Sorting.DESC
            }
        }
        getSortingDeals()
    }

    fun sortingByPrice(priceFrom: Int, priceTo: Int) {
        this.priceFrom = priceFrom
        this.priceTo = priceTo
        getSortingDeals()
    }

    fun getPriceFrom() = priceFrom
    fun getPriceTo() = priceTo

    fun sortingByDiscount(discountFrom: Int, discountTo: Int) {
        this.discountFrom = discountFrom
        this.discountTo = discountTo
        getSortingDeals()
    }

    fun getDiscountFrom() = discountFrom
    fun getDiscountTo() = discountTo

    private fun getSortingDeals() {
        viewModelScope.launchWithLoading {
            dealsRepository.getSortingDeals(
                page = currentPage.toString(),
                categoryType = currentCategoryType,
                taxSlug = taxSlug,
                sorting = sorting,
                sortBy = sortBy,
                priceFrom = priceFrom,
                priceTo = priceTo,
                discountFrom = discountFrom,
                discountTo = discountTo
            )
                .onSuccess { deals ->
                    _deals.postValue(deals.toParcelableList())
                }
                .onFailure {
                    log(it.toString())
                    error.postValue(it.toString())
                }
        }
    }
}