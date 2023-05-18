package com.digeltech.appdiscountone.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.data.source.remote.KEY_HOME_CATEGORIES
import com.digeltech.appdiscountone.domain.model.CategoryWithDeals
import com.digeltech.appdiscountone.ui.common.SEARCH_DELAY
import com.digeltech.appdiscountone.ui.common.getAllDealsFromCache
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.ui.common.model.toParcelable
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
        getBanners()
    }

    fun getDeal(dealId: Int, categoryId: Int) {
        viewModelScope.launchWithLoading {
            val deal = interactor.getDeal(dealId = dealId, categoryId = categoryId).toParcelable()
            _deal.value = deal
        }
    }

    fun deleteDeal() {
        _deal.value = null
    }

    fun searchDeals(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()
        val searchResults = mutableListOf<DealParcelable>()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)
            val listOfDeals = getAllDealsFromCache()
            listOfDeals.forEach {
                if (it.title.contains(searchText, true)) {
                    searchResults.add(it.toParcelable())
                    log("Find this deal ${it.title}")
                }
            }
            _searchResult.value = searchResults
        }
    }

    private fun getBanners() {
        var listOfBanners: List<Banner>
        if (Hawk.contains(KEY_BANNERS)) {
            listOfBanners = Hawk.get(KEY_BANNERS)
            setupBanners(listOfBanners.toMutableList())
        } else {
            viewModelScope.launch {
                listOfBanners = interactor.getBanners()
                setupBanners(listOfBanners.toMutableList())
                Hawk.put(KEY_BANNERS, listOfBanners)
            }
        }

        if (Hawk.contains(KEY_HOME_CATEGORIES)) {
            _categories.value = Hawk.get(KEY_HOME_CATEGORIES)
        } else {
            getInitCategories()
        }
    }

    private fun getInitCategories() {
        viewModelScope.launchWithLoading {
            val listOfCategories = interactor.getInitCategories()
            _categories.postValue(listOfCategories)
            getAllCategories()
        }
    }

    private fun getAllCategories() {
        viewModelScope.launch {
            val listOfCategories = interactor.getAllCategories()
            _categories.postValue(listOfCategories)
        }
    }

    private fun setupBanners(listOfBanners: MutableList<Banner>) {
        // последний из списка баннеров отображаться отдельно
        _soloBanner.value = listOfBanners.last()
        listOfBanners.removeLast()

        _banners.value = listOfBanners
    }
}