package com.digeltech.appdiscountone.ui.shops

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.model.Shop
import com.digeltech.appdiscountone.ui.common.SEARCH_DELAY
import com.digeltech.appdiscountone.ui.common.model.toParcelableList
import com.digeltech.appdiscountone.ui.shops.interactor.ShopsInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopsViewModel @Inject constructor(
    private val shopsInteractor: ShopsInteractor
) : BaseViewModel() {

    private val _shops: MutableLiveData<List<Shop>> = MutableLiveData()
    val shops: LiveData<List<Shop>> = _shops

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

    fun searchDeals(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)

            val deals = shopsInteractor.searchDeals(searchText)
            searchResult.value = deals.toParcelableList()
        }
    }
}