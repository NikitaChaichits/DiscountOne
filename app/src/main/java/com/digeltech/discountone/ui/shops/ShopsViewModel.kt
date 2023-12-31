package com.digeltech.discountone.ui.shops

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.model.Shop
import com.digeltech.discountone.ui.common.SEARCH_DELAY
import com.digeltech.discountone.ui.common.getUserId
import com.digeltech.discountone.ui.common.model.toParcelableList
import com.digeltech.discountone.ui.shops.interactor.ShopsInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopsViewModel @Inject constructor(
    private val interactor: ShopsInteractor
) : BaseViewModel() {

    private val _shops: MutableLiveData<List<Shop>> = MutableLiveData()
    val shops: LiveData<List<Shop>> = _shops

    fun getShopsList() {
        viewModelScope.launch {
            interactor.getShopsList()
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

            launchWithLoading {
                val deals = interactor.searchDeals(searchText)
                searchResult.value = deals.toParcelableList()
            }
        }
    }

    fun updateBookmark(dealId: String) {
        getUserId()?.let { userId ->
            viewModelScope.launch {
                interactor.updateBookmark(userId, dealId)
            }
        }
    }
}