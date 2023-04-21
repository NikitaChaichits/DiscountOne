package com.digeltech.appdiscountone.ui.home

import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.model.CategoryWithDeals
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.ui.common.model.toParcelable
import com.digeltech.appdiscountone.ui.home.adapter.Banner
import com.digeltech.appdiscountone.ui.home.interactor.HomeInteractor
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val KEY_BANNERS = "all-banners-categories"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val interactor: HomeInteractor,
) : BaseViewModel() {

    private val _banners = MutableStateFlow<List<Banner>>(listOf())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()

    private val _soloBanner = MutableStateFlow<Banner?>(null)
    val soloBanner: StateFlow<Banner?> = _soloBanner.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryWithDeals>>(listOf())
    val categories: StateFlow<List<CategoryWithDeals>> = _categories.asStateFlow()

    private val _deal = MutableStateFlow<DealParcelable?>(null)
    val deal: StateFlow<DealParcelable?> = _deal.asStateFlow()

    init {
        getBanners()
    }


    fun getDeal(dealId: Int) {
        viewModelScope.launchWithLoading {
            val deal = interactor.getDeal(dealId).toParcelable()
            _deal.value = deal
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

        getCategories()
    }

    private fun getCategories() {
        viewModelScope.launchWithLoading {
            val listOfCategories = interactor.getCategories()
            _categories.emit(listOfCategories)
        }
    }

    private fun setupBanners(listOfBanners: MutableList<Banner>) {
        // последний из списка баннеров отображаться отдельно
        _soloBanner.value = listOfBanners.last()
        listOfBanners.removeLast()

        _banners.value = listOfBanners
    }
}