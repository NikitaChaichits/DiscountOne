package com.digeltech.appdiscountone.ui.deal

import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.ui.common.getSimilarDealsFromCache
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.ui.common.model.toParcelableList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DealViewModel @Inject constructor() : BaseViewModel() {

    private val _similarDeals = MutableStateFlow<List<DealParcelable>>(listOf())
    val similarDeals: StateFlow<List<DealParcelable>> = _similarDeals.asStateFlow()

    fun getSimilarDeals(categoryId: Int, dealId: Int) {
        viewModelScope.launch {
            val listOfDeals = getSimilarDealsFromCache(dealId = dealId, categoryId = categoryId)
            if (listOfDeals.isNotEmpty()) _similarDeals.emit(listOfDeals.toParcelableList())
        }
    }
}