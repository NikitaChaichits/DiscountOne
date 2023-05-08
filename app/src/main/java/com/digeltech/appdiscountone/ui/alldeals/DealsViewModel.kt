package com.digeltech.appdiscountone.ui.alldeals

import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.repository.DealsRepository
import com.digeltech.appdiscountone.ui.common.DEALS_PAGE_SIZE
import com.digeltech.appdiscountone.ui.common.SCREEN_DEALS_SIZE
import com.digeltech.appdiscountone.ui.common.SEARCH_DELAY
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.ui.common.model.toParcelableList
import com.digeltech.appdiscountone.util.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DealsViewModel @Inject constructor(
    private val dealsRepository: DealsRepository
) : BaseViewModel() {

    private val _deals = MutableStateFlow<List<DealParcelable>>(listOf())
    val deals: StateFlow<List<DealParcelable>> = _deals.asStateFlow()

    private val _searchResult = MutableStateFlow<List<DealParcelable>>(listOf())
    val searchResult: StateFlow<List<DealParcelable>> = _searchResult.asStateFlow()

    private var searchJob: Job? = null
    private var loadDealsJob: Job? = null

    private var currentOffset = 0

    fun initDeals() {
        viewModelScope.launchWithLoading {
            val listOfDeals = dealsRepository.getAllDeals(limit = DEALS_PAGE_SIZE, offset = currentOffset)
            _deals.emit(listOfDeals.toParcelableList())
            currentOffset += DEALS_PAGE_SIZE
            getNextDeals()
        }
    }

    fun searchDeals(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()
        if (loadDealsJob?.isActive == true) loadDealsJob?.cancel()
        val searchResults = mutableListOf<DealParcelable>()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)
            deals.value.forEach {
                if (it.title.contains(searchText, true)) {
                    searchResults.add(it)
                    log("Find this deal ${it.title}")
                }
            }
            _searchResult.value = searchResults
        }
    }

    fun getNextDeals() {
        loadDealsJob = viewModelScope.launch {
            val newListOfDeals = dealsRepository.getAllDeals(limit = DEALS_PAGE_SIZE, offset = currentOffset)
                .toParcelableList()
                .toMutableList()

            newListOfDeals.addAll(_deals.value)
            _deals.emit(newListOfDeals.sortedByDescending { it.id })
            currentOffset += DEALS_PAGE_SIZE
            if (_deals.value.size < SCREEN_DEALS_SIZE) // TODO костыль чтобы не грузилось больше 100 купонов на экране
                getNextDeals()
        }
    }

}