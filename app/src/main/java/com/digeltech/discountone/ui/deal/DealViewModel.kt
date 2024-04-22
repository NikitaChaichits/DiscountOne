package com.digeltech.discountone.ui.deal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.model.Price
import com.digeltech.discountone.domain.repository.DealsRepository
import com.digeltech.discountone.ui.common.getUserId
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.ui.common.model.DealType
import com.digeltech.discountone.ui.common.model.toParcelable
import com.digeltech.discountone.ui.common.model.toParcelableList
import com.digeltech.discountone.util.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DealViewModel @Inject constructor(
    private val dealsRepository: DealsRepository,
) : BaseViewModel() {

    private val _similarCategoryDeals: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val similarCategoryDeals: LiveData<List<DealParcelable>> = _similarCategoryDeals

    private val _similarShopDeals: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val similarShopDeals: LiveData<List<DealParcelable>> = _similarShopDeals

    private val _deal: MutableLiveData<DealParcelable> = MutableLiveData()
    val deal: LiveData<DealParcelable> = _deal

    private val _prices: MutableLiveData<List<Price>> = MutableLiveData()
    val prices: LiveData<List<Price>> = _prices

    val loadingDealError = MutableLiveData<String>()

    fun getSimilarDealsByCategory(dealId: Int, categorySlug: String) {
        viewModelScope.launch {
            dealsRepository.getSimilarDealsByCategory(categorySlug)
                .onSuccess { list ->
                    list.toMutableList().removeIf { it.id == dealId }
                    _similarCategoryDeals.postValue(list.toParcelableList())
                }
                .onFailure(::log)
        }
    }

    fun getSimilarCouponsByCategory(dealId: Int) {
        viewModelScope.launch {
            dealsRepository.getSimilarCouponsByCategory()
                .onSuccess { list ->
                    list.toMutableList().removeIf { it.id == dealId }
                    _similarCategoryDeals.postValue(list.toParcelableList())
                }
                .onFailure(::log)
        }
    }

    fun getSimilarDealsByShop(shopSlug: String, dealId: Int, dealType: DealType) {
        viewModelScope.launch {
            if (dealType == DealType.COUPONS) {
                dealsRepository.getSimilarCouponsByShop(shopSlug)
                    .onSuccess { list ->
                        list.toMutableList().removeIf { it.id == dealId }
                        _similarShopDeals.postValue(list.toParcelableList())
                    }
            } else {
                dealsRepository.getSimilarDealsByShop(shopSlug)
                    .onSuccess { list ->
                        list.toMutableList().removeIf { it.id == dealId }
                        _similarShopDeals.postValue(list.toParcelableList())
                    }
            }

        }
    }

    fun getDeal(id: Int) {
        viewModelScope.launchWithLoading {
            dealsRepository.getDealById(id)
                .onSuccess {
                    _deal.postValue(it.toParcelable())
                }
                .onFailure {
                    loadingDealError.value = ""
                }
        }
    }

    fun activateDealClick(id: String) {
        viewModelScope.launch {
            dealsRepository.activateDealClick(id)
        }
    }

    fun updateBookmark(dealId: String) {
        getUserId()?.let { userId ->
            viewModelScope.launch {
                dealsRepository.updateBookmark(userId, dealId)
            }
        }
    }

    fun getPriceChanges(parsId: Int) {
        viewModelScope.launch {
            dealsRepository.getPriceChanges(parsId.toString())
                .onSuccess(_prices::postValue)
                .onFailure(::log)
        }
    }
}