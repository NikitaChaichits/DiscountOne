package com.digeltech.discountone.ui.profile.wishlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.repository.DealsRepository
import com.digeltech.discountone.ui.common.SEARCH_DELAY
import com.digeltech.discountone.ui.common.getListOfBookmarks
import com.digeltech.discountone.ui.common.getUserId
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.ui.common.model.toParcelableList
import com.digeltech.discountone.util.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedPublicationsViewModel @Inject constructor(
    private val dealsRepository: DealsRepository
) : BaseViewModel() {

    private val _deals: MutableLiveData<List<DealParcelable>> = MutableLiveData()
    val deals: LiveData<List<DealParcelable>> = _deals

    val loadingError = MutableLiveData<Boolean>()
    val isGroupEmptyWishlistVisible = MutableLiveData<Boolean>()

    fun getSavedPublications() {
        if (getListOfBookmarks()?.isNotEmpty() == true) {
            getListOfBookmarks()?.let { bookmarks ->
                _deals.postValue(bookmarks.toMutableList())

                getUserId()?.let { userId ->
                    viewModelScope.launch {
                        dealsRepository.getBookmarksDeals(userId)
                            .onSuccess {
                                val bookmarksOnServer = it.toParcelableList().toSet()
                                val uniqueBookmarks = bookmarks.toSet() - bookmarksOnServer
                                uniqueBookmarks.forEach { deal ->
                                    dealsRepository.addDealToBookmark(userId, deal.id.toString())
                                }
                            }
                    }
                }
            }
        } else {
            isGroupEmptyWishlistVisible.postValue(true)
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
}