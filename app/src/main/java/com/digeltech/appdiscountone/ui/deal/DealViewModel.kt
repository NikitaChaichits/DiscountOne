package com.digeltech.appdiscountone.ui.deal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.repository.DealsRepository
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.ui.common.model.toParcelableList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DealViewModel @Inject constructor(
    private val dealsRepository: DealsRepository,
) : BaseViewModel() {

    private val _similarDeals: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val similarDeals: LiveData<List<DealParcelable>> = _similarDeals

    fun getSimilarDeals(categoryId: Int, dealId: Int) {
        viewModelScope.launch {
            val listOfDeals = dealsRepository.getDealsByCategoryId(categoryId)
            listOfDeals.toMutableList().removeIf { it.id == dealId }

            if (listOfDeals.size >= 5) {
                _similarDeals.postValue(listOfDeals.toParcelableList().shuffled().take(5))
            }
        }
    }
}