package com.digeltech.discountone.ui.deal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.model.Category
import com.digeltech.discountone.domain.model.Shop
import com.digeltech.discountone.domain.repository.CategoriesRepository
import com.digeltech.discountone.domain.repository.DealsRepository
import com.digeltech.discountone.domain.repository.ShopsRepository
import com.digeltech.discountone.ui.common.KEY_CATEGORIES
import com.digeltech.discountone.ui.common.KEY_SHOPS
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.ui.common.model.toParcelable
import com.digeltech.discountone.ui.common.model.toParcelableList
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DealViewModel @Inject constructor(
    private val dealsRepository: DealsRepository,
    private val shopRepository: ShopsRepository,
    private val categoriesRepository: CategoriesRepository
) : BaseViewModel() {

    private val _similarCategoryDeals: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val similarCategoryDeals: LiveData<List<DealParcelable>> = _similarCategoryDeals

    private val _similarShopDeals: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val similarShopDeals: LiveData<List<DealParcelable>> = _similarShopDeals

    private val _deal: MutableLiveData<DealParcelable> = MutableLiveData()
    val deal: LiveData<DealParcelable> = _deal

    val loadingDealError = MutableLiveData<String>()

    fun getSimilarDealsByCategory(categoryId: Int, dealId: Int) {
        if (Hawk.contains(KEY_CATEGORIES)) {
            Hawk.get<List<Category>>(KEY_CATEGORIES).find {
                it.id == categoryId
            }?.slug?.let { slug ->
                viewModelScope.launch {
                    val listOfDeals = dealsRepository.getSimilarDealsByCategory(slug)
                    listOfDeals.toMutableList().removeIf { it.id == dealId }
                    _similarCategoryDeals.postValue(listOfDeals.toParcelableList())
                }
            }
        } else {
            viewModelScope.launch {
                categoriesRepository.getAllCategories().onSuccess {
                    Hawk.get<List<Category>>(KEY_CATEGORIES).find {
                        it.id == categoryId
                    }?.slug?.let { slug ->
                        viewModelScope.launch {
                            val listOfDeals = dealsRepository.getSimilarDealsByCategory(slug)
                            listOfDeals.toMutableList().removeIf { it.id == dealId }
                            _similarCategoryDeals.postValue(listOfDeals.toParcelableList())
                        }
                    }
                }
            }
        }
    }

    fun getSimilarDealsByShop(shopName: String, dealId: Int) {
        if (Hawk.contains(KEY_SHOPS)) {
            Hawk.get<List<Shop>>(KEY_SHOPS).find {
                it.name.equals(shopName, true)
            }?.slug?.let { slug ->
                viewModelScope.launch {
                    val listOfDeals = dealsRepository.getSimilarDealsByShop(slug)
                    listOfDeals.toMutableList().removeIf { it.id == dealId }
                    _similarShopDeals.postValue(listOfDeals.toParcelableList())
                }
            }
        } else {
            viewModelScope.launch {
                shopRepository.getAllShops().onSuccess {
                    Hawk.get<List<Shop>>(KEY_SHOPS).find {
                        it.name.equals(shopName, true)
                    }?.slug?.let { slug ->
                        viewModelScope.launch {
                            val listOfDeals = dealsRepository.getSimilarDealsByShop(slug)
                            listOfDeals.toMutableList().removeIf { it.id == dealId }
                            _similarShopDeals.postValue(listOfDeals.toParcelableList())
                        }
                    }
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

    fun updateDealViewsClick(id: String) {
        viewModelScope.launch {
            dealsRepository.updateDealViewsClick(id)
        }
    }

    fun addBookmark(userId: String, dealId: String) {
        viewModelScope.launch {
            dealsRepository.addDealToBookmark(userId, dealId)
        }
    }

    fun deleteBookmark(userId: String, dealId: String) {
        viewModelScope.launch {
            dealsRepository.deleteDealFromBookmark(userId, dealId)
        }
    }
}