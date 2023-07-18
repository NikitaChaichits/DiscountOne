package com.digeltech.discountone.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.model.Category
import com.digeltech.discountone.ui.categories.interactor.CategoriesInteractor
import com.digeltech.discountone.ui.common.SEARCH_DELAY
import com.digeltech.discountone.ui.common.model.toParcelableList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoriesInteractor: CategoriesInteractor,
) : BaseViewModel() {

    private val _categories: MutableLiveData<List<Pair<Category, Category?>>> = MutableLiveData()
    val categories: LiveData<List<Pair<Category, Category?>>> = _categories

    fun getCategoriesList() {
        viewModelScope.launchWithLoading {
            categoriesInteractor.getCategoriesList()
                .onSuccess {
                    val list = mutableListOf<Pair<Category, Category?>>()
                    for (i in 1..it.size step 2) {
                        if (i + 1 <= it.size)
                            list.add(Pair(it[i.dec()], it[i]))
                        else
                            list.add(Pair(it[i.dec()], null))
                    }
                    _categories.postValue(list)
                }
                .onFailure { error.postValue(it.toString()) }
        }
    }

    fun searchDeals(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)

            launchWithLoading {
                val deals = categoriesInteractor.searchDeals(searchText)
                searchResult.value = deals.toParcelableList()
            }
        }
    }

}