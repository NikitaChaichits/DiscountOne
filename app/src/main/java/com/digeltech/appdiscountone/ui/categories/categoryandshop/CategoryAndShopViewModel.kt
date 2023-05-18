package com.digeltech.appdiscountone.ui.categories.categoryandshop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.ui.categories.interactor.CategoriesInteractor
import com.digeltech.appdiscountone.ui.common.DEALS_PAGE_SIZE
import com.digeltech.appdiscountone.ui.common.SEARCH_DELAY
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.ui.common.model.toParcelableList
import com.digeltech.appdiscountone.util.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryAndShopViewModel @Inject constructor(
    private val categoriesInteractor: CategoriesInteractor
) : BaseViewModel() {

    private val _deals: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val deals: LiveData<List<DealParcelable>> = _deals

    private val _searchResult: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val searchResult: LiveData<List<DealParcelable>> = _searchResult

    private var searchJob: Job? = null
    private var loadDealsJob: Job? = null

    private var currentOffset = 0
    private var isScreenInit = false

    fun initDeals(categoryId: Int) {
        if (isScreenInit && !deals.value.isNullOrEmpty()) {
            viewModelScope.launch {
                getNextDeals(categoryId)
            }
        } else {
            viewModelScope.launchWithLoading {
                val listOfDeals = categoriesInteractor.getCategoryDealsList(categoryId, limit = DEALS_PAGE_SIZE)
                _deals.postValue(listOfDeals.toParcelableList())
                currentOffset += DEALS_PAGE_SIZE
                isScreenInit = true
                getNextDeals(categoryId)
            }
        }
    }

    fun getNextDeals(categoryId: Int) {
        loadDealsJob = viewModelScope.launch {
            val newListOfDeals = categoriesInteractor.getCategoryDealsList(
                categoryId = categoryId,
                limit = DEALS_PAGE_SIZE,
                offset = currentOffset
            )
                .toParcelableList()
                .toMutableList()

            if (newListOfDeals.isNotEmpty()) {
                _deals.value?.let { newListOfDeals.addAll(it) }
                _deals.postValue(newListOfDeals.sortedByDescending { it.id })
                currentOffset += DEALS_PAGE_SIZE
            }
            getNextDeals(categoryId)
        }
    }

    fun stopLoadingDeals() {
        if (loadDealsJob?.isActive == true) loadDealsJob?.cancel()
    }

    fun searchDeals(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()
        stopLoadingDeals()
        val searchResults = mutableListOf<DealParcelable>()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)
            _deals.value?.forEach {
                if (it.title.contains(searchText, true)) {
                    searchResults.add(it)
                    log("Find this deal ${it.title}")
                }
            }
            _searchResult.value = searchResults
        }
    }
}