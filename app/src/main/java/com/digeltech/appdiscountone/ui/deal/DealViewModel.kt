package com.digeltech.appdiscountone.ui.deal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.model.Category
import com.digeltech.appdiscountone.domain.model.Shop
import com.digeltech.appdiscountone.domain.repository.DealsRepository
import com.digeltech.appdiscountone.ui.common.KEY_CATEGORIES
import com.digeltech.appdiscountone.ui.common.KEY_SHOPS
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.ui.common.model.toParcelableList
import com.orhanobut.hawk.Hawk
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

    fun getSimilarDealsByCategory(categoryId: Int, dealId: Int) {
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

    fun getSimilarDealsByShop(shopName: String, dealId: Int) {
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