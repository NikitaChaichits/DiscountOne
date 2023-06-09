package com.digeltech.appdiscountone.ui.categories.categoryandshop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.repository.DealsRepository
import com.digeltech.appdiscountone.ui.common.SEARCH_DELAY
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.ui.common.model.toParcelableList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryAndShopViewModel @Inject constructor(
    private val dealsRepository: DealsRepository,
) : BaseViewModel() {

    private val _deals: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val deals: LiveData<List<DealParcelable>> = _deals

    private val _searchResult: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val searchResult: LiveData<List<DealParcelable>> = _searchResult

    private var searchJob: Job? = null

    fun initDeals(id: Int, isFromCategory: Boolean) {
        viewModelScope.launchWithLoading {
            val listOfDeals = if (isFromCategory) {
                dealsRepository.getDealsByCategoryId(id)
            } else {
                dealsRepository.getDealsByShopId(id)
            }
            _deals.postValue(listOfDeals.toParcelableList().sortedByDescending { it.id })
        }
    }

    fun searchDeals(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)

            val deals = dealsRepository.searchDeals(searchText)
            _searchResult.value = deals.toParcelableList()
        }
    }

    fun updateDealViewsClick(id: String) {
        viewModelScope.launch {
            dealsRepository.updateDealViewsClick(id)
        }
    }
}