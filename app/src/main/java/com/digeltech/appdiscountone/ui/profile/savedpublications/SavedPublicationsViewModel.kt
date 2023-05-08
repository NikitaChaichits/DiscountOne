package com.digeltech.appdiscountone.ui.profile.savedpublications

import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.ui.common.SEARCH_DELAY
import com.digeltech.appdiscountone.ui.common.getListOfBookmarks
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
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
class SavedPublicationsViewModel @Inject constructor() : BaseViewModel() {

    private val _deals = MutableStateFlow<List<DealParcelable>>(listOf())
    val deals: StateFlow<List<DealParcelable>> = _deals.asStateFlow()

    private val _searchResult = MutableStateFlow<List<DealParcelable>>(listOf())
    val searchResult: StateFlow<List<DealParcelable>> = _searchResult.asStateFlow()

    private var searchJob: Job? = null

    fun getSavedPublications() {
        viewModelScope.launch {
            getListOfBookmarks()?.let { _deals.emit(it.toMutableList()) }
        }
    }

    fun searchDeals(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()
        val searchResults = mutableListOf<DealParcelable>()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)
            deals.value.forEach {
                if (it.title.contains(searchText, true)) {
                    searchResults.add(it)
                    log("Find this deal ${it.title}")
                }
            }
            _searchResult.value = searchResults
        }
    }
}