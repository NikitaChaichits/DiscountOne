package com.digeltech.discountone.ui.categoryandshop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.model.CategoryShopFilterItem
import com.digeltech.discountone.ui.categoryandshop.interactor.CategoryAndShopInteractor
import com.digeltech.discountone.ui.common.SEARCH_DELAY
import com.digeltech.discountone.ui.common.getUserId
import com.digeltech.discountone.ui.common.model.*
import com.digeltech.discountone.util.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryAndShopViewModel @Inject constructor(
    private val interactor: CategoryAndShopInteractor,
) : BaseViewModel() {

    private val _deals: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val deals: LiveData<List<DealParcelable>> = _deals

    private val _categoryOrShopNames: MutableLiveData<List<CategoryShopFilterItem>> = MutableLiveData()
    val categoryOrShopNames: LiveData<List<CategoryShopFilterItem>> = _categoryOrShopNames

    val filteringError = MutableLiveData<String>()

    private val allDeals = mutableListOf<DealParcelable>()
    private var nextPage = 3 // startup count of deal = 100, loading per page = 50, so nextPage is 3
    private var currentCategoryType: CategoryType = CategoryType.SHOP
    private lateinit var taxSlug: String
    private var currentSortBySpinnerPosition = 0
    private var currentCatOrShopSpinnerPosition = 0
    private var categoryOrShopSlug = ""
    private var priceFrom = 0
    private var priceTo = 0
    private var sorting = Sorting.DESC
    private var sortBy = SortBy.DATE

    private var sortingJob: Job? = null

    fun initScreenData(slug: String, isFromCategory: Boolean, id: String) {
        if (isFromCategory) currentCategoryType = CategoryType.CATEGORY
        taxSlug = slug
        viewModelScope.launch {
            if (isFromCategory) {
                interactor.getCategoryShops(slug)
                    .onSuccess {
                        _categoryOrShopNames.value = it
                    }
            } else {
                interactor.getShopCategories(slug)
                    .onSuccess {
                        _categoryOrShopNames.value = it
                    }
            }
            launchWithLoading {
                interactor.getInitialDeals(currentCategoryType, id)
                    .onSuccess {
                        _deals.postValue(it.toParcelableList())
                        allDeals.addAll(it.toParcelableList())
                    }
                    .onFailure {
                        error.postValue(it.toString())
                    }
            }
        }
    }

    fun searchDeals(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)

            launchWithLoading {
                val deals = interactor.searchDeals(searchText)
                searchResult.value = deals.toParcelableList()
            }
        }
    }

    fun sortingByType(spinnerPosition: Int) {
        currentSortBySpinnerPosition = spinnerPosition
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
        nextPage = 3
        getSortingDeals()
    }

    fun sortingByPrice(priceFrom: Int, priceTo: Int) {
        this.priceFrom = priceFrom
        this.priceTo = priceTo
        nextPage = 3
        getSortingDeals()
    }

    fun sortingByCatOrShop(spinnerPosition: Int) {
        currentCatOrShopSpinnerPosition = spinnerPosition
        categoryOrShopSlug = if (spinnerPosition == 0) {
            ""
        } else {
            _categoryOrShopNames.value?.get(spinnerPosition - 1)?.slug ?: ""
        }
        nextPage = 3
        getSortingDeals()
    }

    fun getPriceFrom() = priceFrom
    fun getPriceTo() = priceTo

    fun getSortBySpinnerPosition(): Int = currentSortBySpinnerPosition

    fun getCatOrShopSpinnerPosition(): Int = currentCatOrShopSpinnerPosition

    fun loadMoreDeals() {
        if (sortingJob?.isActive == true) sortingJob?.cancel()

        sortingJob = viewModelScope.launchWithLoading {
            interactor.getSortingDeals(
                page = nextPage.toString(),
                categoryType = currentCategoryType,
                taxSlug = taxSlug,
                sorting = sorting,
                sortBy = sortBy,
                catOrShopSlug = categoryOrShopSlug.takeIf { it.isNotEmpty() },
                priceFrom = priceFrom,
                priceTo = priceTo
            )
                .onSuccess { deals ->
                    if (deals.isNotEmpty()) {
                        allDeals.addAll(deals.toParcelableList())
                        _deals.postValue(allDeals as List<DealParcelable>)
                        nextPage++
                    }
                }
                .onFailure {
                    log(it.toString())
                    filteringError.postValue(it.toString())
                }
        }
    }

    fun updateBookmark(dealId: String) {
        getUserId()?.let { userId ->
            viewModelScope.launch {
                interactor.updateBookmark(userId, dealId)
            }
        }
    }

    private fun getSortingDeals() {
        if (sortingJob?.isActive == true) sortingJob?.cancel()

        sortingJob = viewModelScope.launchWithLoading {
            interactor.getSortingDeals(
                categoryType = currentCategoryType,
                taxSlug = taxSlug,
                sorting = sorting,
                sortBy = sortBy,
                catOrShopSlug = categoryOrShopSlug.takeIf { it.isNotEmpty() },
                priceFrom = priceFrom,
                priceTo = priceTo
            )
                .onSuccess { deals ->
                    if (deals.isNotEmpty()) {
                        allDeals.clear()
                        allDeals.addAll(deals.toParcelableList())
                        _deals.postValue(allDeals as List<DealParcelable>)
                    }
                }
                .onFailure {
                    log(it.toString())
                    filteringError.postValue(it.toString())
                }
        }
    }

}