package com.digeltech.discountone.ui.bestdeals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.model.Item
import com.digeltech.discountone.domain.repository.DealsRepository
import com.digeltech.discountone.ui.common.SEARCH_DELAY
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.ui.common.model.toParcelableList
import com.digeltech.discountone.util.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ITEMS_ON_PAGE = "50"

@HiltViewModel
class DealsViewModel @Inject constructor(
    private val dealsRepository: DealsRepository
) : BaseViewModel() {

    private val _deals: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val deals: LiveData<List<DealParcelable>> = _deals

    private val _categories: MutableLiveData<List<Item>> = MutableLiveData()
    val categories: LiveData<List<Item>> = _categories

    private val _shops: MutableLiveData<List<Item>> = MutableLiveData()
    val shops: LiveData<List<Item>> = _shops

    private val allDeals = mutableListOf<DealParcelable>()

    private var categorySpinnerPosition = 0
    private var shopSpinnerPosition = 0
    private var selectedShopId = 0
    private var selectedCategoryId = 0
    private var currentPage = 3 // startup count of deal = 100, next loading loading per page = 50, so currentPage is 2

    init {
        initDeals()
    }

    private fun initDeals() {
        viewModelScope.launch {
            loadingGifVisibility.value = true
            dealsRepository.getBestDeals()
                .onSuccess {
                    allDeals.addAll(it.posts.toParcelableList())
                    _deals.postValue(it.posts.toParcelableList())
                    _shops.postValue(it.shops)

                    val categories = mutableListOf<Item>()
                    it.categories.forEach { category ->
                        categories.add(Item(category.id, category.name))
                        category.child.forEach { childItem ->
                            // yes, hardcode, but for child items customer need padding
                            categories.add(Item(childItem.id, "• ${childItem.name}"))
                        }
                    }
                    _categories.postValue(categories)
                }
                .onFailure { error.postValue(it.toString()) }
            loadingGifVisibility.value = false
        }
    }

    fun loadMoreDeals() {
        viewModelScope.launchWithLoading {
            dealsRepository.getAllDeals(currentPage.toString(), ITEMS_ON_PAGE)
                .onSuccess { deals ->
                    if (deals.isNotEmpty()) {
                        allDeals.addAll(deals.toParcelableList())
                        _deals.postValue(allDeals as List<DealParcelable>)
                        currentPage++
                    }
                }
                .onFailure {
                    log(it.toString())
                }
        }
    }

    fun searchDeals(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)

            launchWithLoading {
                val deals = dealsRepository.searchDeals(searchText)
                searchResult.value = deals.toParcelableList()
            }
        }
    }

    fun updateDealViewsClick(id: String) {
        viewModelScope.launch {
            dealsRepository.updateDealViewsClick(id)
        }
    }

    fun loadCategoryDeals(spinnerPosition: Int) {
        categorySpinnerPosition = spinnerPosition
        selectedCategoryId = if (spinnerPosition > 0)
            _categories.value?.get(spinnerPosition - 1)?.id ?: 0
        else 0

        viewModelScope.launch {
            if (spinnerPosition == 0) {
                if (shopSpinnerPosition == 0) {
                    _deals.postValue(allDeals)
                } else {
                    loadShopDeals(shopSpinnerPosition)
                }
            } else {
                viewModelScope.launchWithLoading {
                    _categories.value?.get(spinnerPosition - 1)?.id?.let { categoryId ->
                        dealsRepository.getDealsByCategoryAndShopId(
                            categoryId = categoryId,
                            shopId = selectedShopId.takeIf { shopId -> shopId != 0 })
                            .onSuccess { deals ->
                                _deals.postValue(deals.toParcelableList())
                            }
                            .onFailure { e ->
                                log(e.toString())
                                error.postValue(e.toString())
                            }
                    }
                }
            }
        }
    }

    fun loadShopDeals(spinnerPosition: Int) {
        shopSpinnerPosition = spinnerPosition

        selectedShopId = if (spinnerPosition > 0)
            _shops.value?.get(spinnerPosition - 1)?.id ?: 0
        else 0

        viewModelScope.launch {
            if (spinnerPosition == 0) {
                if (categorySpinnerPosition == 0) {
                    _deals.postValue(allDeals)
                } else {
                    loadCategoryDeals(categorySpinnerPosition)
                }
            } else {
                viewModelScope.launchWithLoading {
                    _shops.value?.get(spinnerPosition - 1)?.id?.let { shopId ->
                        dealsRepository.getDealsByCategoryAndShopId(
                            categoryId = selectedCategoryId.takeIf { categoryId -> categoryId != 0 },
                            shopId = shopId
                        )
                            .onSuccess { deals ->
                                _deals.postValue(deals.toParcelableList())
                            }
                            .onFailure { e ->
                                log(e.toString())
                                error.postValue(e.toString())
                            }
                    }
                }
            }
        }
    }

    fun getCategoriesFilterPosition(): Int = categorySpinnerPosition

    fun getShopFilterPosition(): Int = shopSpinnerPosition
}