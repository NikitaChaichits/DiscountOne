package com.digeltech.discountone.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.model.CategoryWithDeals
import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.domain.model.HomeShop
import com.digeltech.discountone.ui.common.KEY_SAVED_DEALS
import com.digeltech.discountone.ui.common.SEARCH_DELAY
import com.digeltech.discountone.ui.common.getUserId
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.ui.common.model.toParcelableList
import com.digeltech.discountone.ui.home.interactor.HomeInteractor
import com.digeltech.discountone.util.log
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

const val KEY_BANNERS = "all-banners"
const val KEY_HOMEPAGE_DATA = "all-homepage-data"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val interactor: HomeInteractor,
) : BaseViewModel() {

    private val _banners: MutableLiveData<List<Deal>> = MutableLiveData()
    val banners: LiveData<List<Deal>> = _banners

    private val _discounts: MutableLiveData<List<DealParcelable>?> = MutableLiveData()
    val discounts: LiveData<List<DealParcelable>?> = _discounts

    private val _coupons: MutableLiveData<List<DealParcelable>?> = MutableLiveData()
    val coupons: LiveData<List<DealParcelable>?> = _coupons

    private val _finance: MutableLiveData<List<DealParcelable>?> = MutableLiveData()
    val finance: LiveData<List<DealParcelable>?> = _finance

    private val _shops: MutableLiveData<List<HomeShop>?> = MutableLiveData()
    val shops: LiveData<List<HomeShop>?> = _shops

    private val _categories: MutableLiveData<List<CategoryWithDeals>> = MutableLiveData()
    val categories: LiveData<List<CategoryWithDeals>> = _categories

    private val _deal: MutableLiveData<DealParcelable> = MutableLiveData()
    val deal: LiveData<DealParcelable> = _deal

    fun getHomepageData() {
        viewModelScope.launch {
            loadingGifVisibility.value = true
            getUserId()?.let {
                interactor.getFetchListOfBookmarks(it)
                    .onSuccess { list ->
                        Hawk.put(KEY_SAVED_DEALS, list.toParcelableList())
                    }
            }
            interactor.getHomepage()
                .onSuccess {
                    _banners.value = it.listOfBanners
                    _discounts.value = it.discounts.items.toParcelableList()
                    _coupons.value = it.coupons.items.toParcelableList()
                    _finance.value = it.finance.items.toParcelableList()
                    _shops.value = it.shops.filter { shop -> shop.icon != null }
                    _categories.value = it.categories
                }.onFailure {
                    log(it.toString())
                    error.postValue(it.toString())
                }
            loadingGifVisibility.value = false
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

    fun updateDealViewsClick(id: String) {
        viewModelScope.launch {
            interactor.updateDealViewsClick(id)
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