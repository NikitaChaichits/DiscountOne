package com.digeltech.appdiscountone.ui.shops

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.model.Shop
import com.digeltech.appdiscountone.ui.common.SEARCH_DELAY
import com.digeltech.appdiscountone.ui.shops.interactor.ShopsInteractor
import com.digeltech.appdiscountone.util.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopsViewModel @Inject constructor(
    private val shopsInteractor: ShopsInteractor
) : BaseViewModel() {

    private val _shops: MutableLiveData<List<Shop>> = MutableLiveData()
    val shops: LiveData<List<Shop>> = _shops

    private val _searchResult: MutableLiveData<List<Shop>> = MutableLiveData()
    val searchResult: LiveData<List<Shop>> = _searchResult

    private var searchJob: Job? = null

    fun getShopsList() {
        viewModelScope.launchWithLoading {
            shopsInteractor.getShopsList()
                .onSuccess { shops ->
                    _shops.postValue(
                        shops
                            .filter { it.popular }
                            .sortedBy { it.name.lowercase() }
                    )
                }
                .onFailure { error.postValue(it.toString()) }
        }
    }

    fun searchShops(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()
        val searchResults = mutableListOf<Shop>()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)
            shops.value?.forEach {
                if (it.name.contains(searchText, true)) {
                    searchResults.add(it)
                    log("Find this shop ${it.name}")
                }
            }
            _searchResult.postValue(searchResults)
        }
    }
}