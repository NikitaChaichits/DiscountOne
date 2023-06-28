package com.digeltech.appdiscountone.ui.categories.categoryandshop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.repository.DealsRepository
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
    private val dealsRepository: DealsRepository,
) : BaseViewModel() {

    private val _deals: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val deals: LiveData<List<DealParcelable>> = _deals

    private val _searchResult: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val searchResult: LiveData<List<DealParcelable>> = _searchResult

    private var searchJob: Job? = null

    fun initDeals(id: Int, isFromCategory: Boolean) {
        viewModelScope.launchWithLoading {
            if (isFromCategory) {
                dealsRepository.getDealsByCategoryId(id)
                    .onSuccess { deals ->
                        _deals.postValue(deals.toParcelableList().sortedByDescending { it.id })
                    }
                    .onFailure {
                        log(it.toString())
                        error.postValue(it.toString())
                    }
            } else {
                dealsRepository.getDealsByShopId(id)
                    .onSuccess { deals ->
                        _deals.postValue(deals.toParcelableList().sortedByDescending { it.id })
                    }
                    .onFailure {
                        log(it.toString())
                        error.postValue(it.toString())
                    }
            }

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