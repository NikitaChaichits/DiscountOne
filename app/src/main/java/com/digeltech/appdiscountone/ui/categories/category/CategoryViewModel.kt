package com.digeltech.appdiscountone.ui.categories.category

import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.ui.categories.interactor.CategoriesInteractor
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.ui.common.model.toDealParcelableList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoriesInteractor: CategoriesInteractor
) : BaseViewModel() {

    private val _deals = MutableStateFlow<List<DealParcelable>>(listOf())
    val deals: StateFlow<List<DealParcelable>> = _deals.asStateFlow()

    fun getCategoryDeals(categoryId: Int) {
        viewModelScope.launchWithLoading {
            val listOfDeals = categoriesInteractor.getCategoryDealsList(categoryId)
            _deals.emit(listOfDeals.toDealParcelableList())
        }
    }
}