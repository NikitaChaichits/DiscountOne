package com.digeltech.appdiscountone.ui.categories

import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.model.Category
import com.digeltech.appdiscountone.ui.categories.interactor.CategoriesInteractor
import com.digeltech.appdiscountone.ui.common.SEARCH_DELAY
import com.digeltech.appdiscountone.util.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoriesInteractor: CategoriesInteractor,
) : BaseViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(listOf())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _searchResult = MutableStateFlow<List<Category>>(listOf())
    val searchResult: StateFlow<List<Category>> = _searchResult.asStateFlow()

    private var searchJob: Job? = null

    fun getCategoriesList() {
        viewModelScope.launchWithLoading {
            val list = categoriesInteractor.getCategoriesList()
            _categories.emit(list)
        }
    }

    fun searchCategories(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()
        val searchResults = mutableListOf<Category>()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)
            categories.value.forEach {
                if (it.name.contains(searchText, true)) {
                    searchResults.add(it)
                    log("Find this category ${it.name}")
                }
            }
            _searchResult.value = searchResults
        }
    }

}