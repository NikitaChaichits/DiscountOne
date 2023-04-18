package com.digeltech.appdiscountone.ui.shops

import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.model.Shop
import com.digeltech.appdiscountone.ui.shops.interactor.ShopsInteractor
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private const val KEY = "all-shops"

@HiltViewModel
class ShopsViewModel @Inject constructor(
    private val shopsInteractor: ShopsInteractor
) : BaseViewModel() {

    private val _shops = MutableStateFlow<List<Shop>>(listOf())
    val shops: StateFlow<List<Shop>> = _shops.asStateFlow()

    fun getShopsList() {
        if (Hawk.contains(KEY)) {
            _shops.value = Hawk.get(KEY)
        } else {
            viewModelScope.launchWithLoading {
                val list = shopsInteractor.getShopsList()
                Hawk.put(KEY, list)
                _shops.emit(list)
            }
        }
    }
}