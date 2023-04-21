package com.digeltech.appdiscountone.ui.shops

import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.model.Shop
import com.digeltech.appdiscountone.ui.shops.interactor.ShopsInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ShopsViewModel @Inject constructor(
    private val shopsInteractor: ShopsInteractor
) : BaseViewModel() {

    private val _shops = MutableStateFlow<List<Shop>>(listOf())
    val shops: StateFlow<List<Shop>> = _shops.asStateFlow()

    fun getShopsList() {
        viewModelScope.launchWithLoading {
            val list = shopsInteractor.getShopsList()
            _shops.emit(list)
        }
    }
}