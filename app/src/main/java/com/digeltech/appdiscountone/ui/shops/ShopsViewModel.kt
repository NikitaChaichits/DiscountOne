package com.digeltech.appdiscountone.ui.shops

import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.model.Shop
import com.digeltech.appdiscountone.ui.common.SEARCH_DELAY
import com.digeltech.appdiscountone.ui.shops.interactor.ShopsInteractor
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
class ShopsViewModel @Inject constructor(
    private val shopsInteractor: ShopsInteractor
) : BaseViewModel() {

    private val _shops = MutableStateFlow<List<Shop>>(listOf())
    val shops: StateFlow<List<Shop>> = _shops.asStateFlow()

    private val _searchResult = MutableStateFlow<List<Shop>>(listOf())
    val searchResult: StateFlow<List<Shop>> = _searchResult.asStateFlow()

    private var searchJob: Job? = null

    fun getShopsList() {
        viewModelScope.launchWithLoading {
            val list = shopsInteractor.getShopsList()
            _shops.emit(
                list
                    .filter { it.popular }
                    .sortedBy { it.name.lowercase() }
            )
        }
    }

    fun searchShops(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()
        val searchResults = mutableListOf<Shop>()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)
            shops.value.forEach {
                if (it.name.contains(searchText, true)) {
                    searchResults.add(it)
                    log("Find this shop ${it.name}")
                }
            }
            _searchResult.value = searchResults
        }
    }
}