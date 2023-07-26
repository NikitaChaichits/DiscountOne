package com.digeltech.discountone.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.model.CategoryWithSubcategories
import com.digeltech.discountone.domain.model.Deal
import com.digeltech.discountone.ui.common.SEARCH_DELAY
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.ui.common.model.toParcelableList
import com.digeltech.discountone.ui.home.interactor.HomeInteractor
import com.digeltech.discountone.util.log
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

//    private val _soloBanner: MutableLiveData<Banner> = MutableLiveData()
//    val soloBanner: LiveData<Banner?> = _soloBanner

    private val _bestDeals: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val bestDeals: LiveData<List<DealParcelable>> = _bestDeals

    private val _categories: MutableLiveData<List<CategoryWithSubcategories>> = MutableLiveData()
    val categories: LiveData<List<CategoryWithSubcategories>> = _categories

    fun getHomepageData() {
        viewModelScope.launch {
            loadingGifVisibility.value = true
            interactor.getHomepage().onSuccess {
                _banners.value = it.listOfBanners
                _bestDeals.value = it.bestDeals.items.toParcelableList()
                _categories.value = it.categories
            }.onFailure {
                log(it.toString())
                error.postValue(it.toString())
            }
            loadingGifVisibility.value = false
        }
    }

//    fun getAllCategories() {
//        if (allCategories.isNotEmpty()) {
//            viewModelScope.launchWithLoading {
//                _categories.value = allCategories
//                allCategories = emptyList()
//            }
//        }
//    }

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

}