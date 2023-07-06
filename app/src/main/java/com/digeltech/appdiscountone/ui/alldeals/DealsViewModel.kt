package com.digeltech.appdiscountone.ui.alldeals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.model.Item
import com.digeltech.appdiscountone.domain.repository.DealsRepository
import com.digeltech.appdiscountone.ui.common.SEARCH_DELAY
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.ui.common.model.toParcelableList
import com.digeltech.appdiscountone.util.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    private lateinit var allDeals: List<DealParcelable>

    private var categorySpinnerPosition = 0
    private var shopSpinnerPosition = 0

    init {
        initDeals()
    }

    fun initDeals() {
        viewModelScope.launchWithLoading {
            dealsRepository.getBestDeals()
                .onSuccess {
                    allDeals = it.posts.toParcelableList()
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
        }
    }

    fun loadMoreDeals() {
        viewModelScope.launch {
            dealsRepository.getAllDeals()
                .onSuccess {
                    _deals.postValue(it.toParcelableList())
                }
                .onFailure {
                    dealsRepository.getAllDeals()
                }
        }
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

    fun loadCategoryDeals(spinnerPosition: Int) {
        categorySpinnerPosition = spinnerPosition
        viewModelScope.launch {
            if (spinnerPosition == 0) {
                if (shopSpinnerPosition == 0) {
                    _deals.postValue(allDeals)
                } else {
                    loadShopDeals(shopSpinnerPosition)
                }
            } else {
                viewModelScope.launchWithLoading {
                    _categories.value?.get(spinnerPosition - 1)?.id?.let {
                        dealsRepository.getDealsByCategoryId(it)
                            .onSuccess { deals ->
                                if (shopSpinnerPosition == 0) {
                                    _deals.postValue(deals.toParcelableList())
                                } else {
                                    val selectedShopName = _shops.value?.get(shopSpinnerPosition - 1)?.name
                                    _deals.postValue(
                                        deals.toParcelableList().filter { deal ->
                                            deal.shopName.equals(selectedShopName, true)
                                        })
                                }
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
        viewModelScope.launch {
            if (spinnerPosition == 0) {
                if (categorySpinnerPosition == 0) {
                    _deals.postValue(allDeals)
                } else {
                    loadCategoryDeals(categorySpinnerPosition)
                }
            } else {
                viewModelScope.launchWithLoading {
                    _shops.value?.get(spinnerPosition - 1)?.id?.let {
                        dealsRepository.getDealsByShopId(it)
                            .onSuccess { deals ->
                                if (categorySpinnerPosition == 0) {
                                    _deals.postValue(deals.toParcelableList())
                                } else {
                                    val selectedCategoryId = _categories.value?.get(categorySpinnerPosition - 1)?.id
                                    _deals.postValue(
                                        deals.toParcelableList().filter { deal ->
                                            deal.categoryId == selectedCategoryId
                                        })
                                }
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