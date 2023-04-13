package com.digeltech.appdiscountone.ui.categories.category

import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.ui.categories.interactor.CategoriesInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoriesInteractor: CategoriesInteractor
) : BaseViewModel() {

    private val _deals = MutableStateFlow<List<Deal>>(listOf())
    val deals: StateFlow<List<Deal>> = _deals.asStateFlow()

    fun getCategoryDeals(categoryId: Int) {
        viewModelScope.launchWithLoading {
            _deals.emit(categoriesInteractor.getCategoryDealsList(categoryId))
        }
    }
}