package com.digeltech.discountone.ui.profile.wishlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseFilteringViewModel
import com.digeltech.discountone.domain.repository.DealsRepository
import com.digeltech.discountone.ui.common.KEY_SAVED_DEALS
import com.digeltech.discountone.ui.common.SEARCH_DELAY
import com.digeltech.discountone.ui.common.getUserId
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.ui.common.model.toParcelableList
import com.digeltech.discountone.util.log
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedPublicationsViewModel @Inject constructor(
    private val dealsRepository: DealsRepository
) : BaseFilteringViewModel() {

    val loadingError = MutableLiveData<Boolean>()
    val isGroupEmptyWishlistVisible = MutableLiveData<Boolean>()

    fun getSavedPublications() {
        viewModelScope.launchWithLoading {
            getUserId()?.let {
                dealsRepository.getBookmarksDeals(it)
                    .onSuccess { list ->
                        if (list.isNotEmpty()) {
                            Hawk.put(KEY_SAVED_DEALS, list.toParcelableList())
                            deals.postValue(list.toParcelableList())
                        } else {
                            isGroupEmptyWishlistVisible.postValue(true)
                        }
                    }
                    .onFailure {
                        isGroupEmptyWishlistVisible.postValue(true)
                    }
            }
        }
    }

    fun searchDeals(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()
        val searchResults = mutableListOf<DealParcelable>()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)

            launchWithLoading {
                deals.value?.forEach {
                    if (it.title.contains(searchText, true)) {
                        searchResults.add(it)
                        log("Find this deal ${it.title}")
                    }
                }
                searchResult.value = searchResults
            }
        }
    }

    fun updateBookmark(dealId: String) {
        getUserId()?.let { userId ->
            viewModelScope.launch {
                dealsRepository.updateBookmark(userId, dealId)
            }
        }
    }
}