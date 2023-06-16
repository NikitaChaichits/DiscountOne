package com.digeltech.appdiscountone.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.model.CategoryWithDeals
import com.digeltech.appdiscountone.ui.common.SEARCH_DELAY
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.ui.common.model.toParcelable
import com.digeltech.appdiscountone.ui.common.model.toParcelableList
import com.digeltech.appdiscountone.ui.home.adapter.Banner
import com.digeltech.appdiscountone.ui.home.interactor.HomeInteractor
import com.digeltech.appdiscountone.util.log
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

const val KEY_BANNERS = "all-banners"
const val KEY_HOMEPAGE_DATA = "all-homepage-data"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val interactor: HomeInteractor,
) : BaseViewModel() {

    private val _banners: MutableLiveData<List<Banner>> = MutableLiveData()
    val banners: LiveData<List<Banner>> = _banners

    private val _soloBanner: MutableLiveData<Banner> = MutableLiveData()
    val soloBanner: LiveData<Banner?> = _soloBanner

    private val _categories: MutableLiveData<List<CategoryWithDeals>> = MutableLiveData()
    val categories: LiveData<List<CategoryWithDeals>> = _categories

    private val _deal: MutableLiveData<DealParcelable?> = MutableLiveData()
    val deal: LiveData<DealParcelable?> = _deal

    private val _searchResult: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val searchResult: LiveData<List<DealParcelable>> = _searchResult

    private var searchJob: Job? = null

    init {
        getHomepageData()
    }

    fun getHomepageData() {
        viewModelScope.launchWithLoading {
            interactor.getHomepage().onSuccess {
                _soloBanner.value = it.soloBanner
                _banners.value = it.listOfBanners
                _categories.value = it.categories
                Hawk.put(KEY_HOMEPAGE_DATA, it)
            }.onFailure {
                log(it.toString())
                error.postValue(it.toString())
            }
        }
    }

    fun getDeal(dealId: Int?, categoryId: Int?) {
        viewModelScope.launchWithLoading {
            if (dealId != null && categoryId != null) {
                val deal = interactor.getDeal(dealId = dealId, categoryId = categoryId).toParcelable()
                _deal.value = deal
            } else {
                error.postValue("Cannot load Deal: dealId or categoryId is null")
            }
        }
    }

    fun deleteDeal() {
        _deal.value = null
    }

    fun searchDeals(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)

            val deals = interactor.searchDeals(searchText)
            _searchResult.value = deals.toParcelableList()
        }
    }

    fun updateDealViewsClick(id: String) {
        viewModelScope.launch {
            interactor.updateDealViewsClick(id)
        }
    }

}