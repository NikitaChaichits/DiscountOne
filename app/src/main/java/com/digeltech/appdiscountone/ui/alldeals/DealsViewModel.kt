package com.digeltech.appdiscountone.ui.alldeals

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
class DealsViewModel @Inject constructor(
    private val dealsRepository: DealsRepository
) : BaseViewModel() {

    private val _deals: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val deals: LiveData<List<DealParcelable>> = _deals

    private val _searchResult: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val searchResult: LiveData<List<DealParcelable>> = _searchResult

    private var searchJob: Job? = null
    private var loadDealsJob: Job? = null

//    private var currentOffset = 0

    init {
        initDeals()
    }

    fun initDeals() {
        viewModelScope.launchWithLoading {
            val listOfDeals = dealsRepository.getAllDeals()
            _deals.postValue(listOfDeals.toParcelableList().sortedByDescending { it.viewsClick })
//            currentOffset += DEALS_PAGE_SIZE
        }
    }

//    fun getNextDeals() {
//        loadDealsJob = viewModelScope.launch {
//            val newListOfDeals = dealsRepository.getAllDeals()
//                .toParcelableList()
//                .toMutableList()
//            if (newListOfDeals.isNotEmpty()) {
//                _deals.value?.let { newListOfDeals.addAll(it) }
//                _deals.postValue(newListOfDeals.sortedByDescending { it.id })
//                currentOffset += DEALS_PAGE_SIZE
//            }
//            getNextDeals()
//        }
//    }

    fun stopLoadingDeals() {
        if (loadDealsJob?.isActive == true) loadDealsJob?.cancel()
    }

    fun searchDeals(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()
        stopLoadingDeals()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)

            val deals = dealsRepository.searchDeals(searchText)
            _searchResult.value = deals.toParcelableList()
        }
    }

}