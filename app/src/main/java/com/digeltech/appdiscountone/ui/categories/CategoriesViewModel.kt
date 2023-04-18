package com.digeltech.appdiscountone.ui.categories

import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.model.Category
import com.digeltech.appdiscountone.ui.categories.interactor.CategoriesInteractor
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private const val KEY = "all-categories"

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoriesInteractor: CategoriesInteractor,
) : BaseViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(listOf())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    fun getCategoriesList() {
        if (Hawk.contains(KEY)) {
            _categories.value = Hawk.get(KEY)
        } else {
            viewModelScope.launchWithLoading {
                val list = categoriesInteractor.getCategoriesList()
                Hawk.put(KEY, list)
                _categories.emit(list)
            }
        }
    }

}