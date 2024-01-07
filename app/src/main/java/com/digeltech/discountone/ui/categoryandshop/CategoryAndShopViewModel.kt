package com.digeltech.discountone.ui.categoryandshop

import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseFilteringViewModel
import com.digeltech.discountone.domain.model.getTaxonomyBySlug
import com.digeltech.discountone.ui.categoryandshop.interactor.CategoryAndShopInteractor
import com.digeltech.discountone.ui.common.SEARCH_DELAY
import com.digeltech.discountone.ui.common.getUserId
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.ui.common.model.DealType
import com.digeltech.discountone.ui.common.model.Taxonomy
import com.digeltech.discountone.ui.common.model.toParcelableList
import com.digeltech.discountone.util.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryAndShopViewModel @Inject constructor(
    private val interactor: CategoryAndShopInteractor,
) : BaseFilteringViewModel() {

    private val allDeals = mutableListOf<DealParcelable>()
    private var currentCategoryType: Taxonomy = Taxonomy.SHOP

    fun initScreenData(slug: String, id: String) {
        if (allDeals.isEmpty()) {
//            loadingGifVisibility.value = true
            shopSlug = slug
            viewModelScope.launch {
                interactor.getShopCategories(slug)
                    .onSuccess {
                        val updatedItemList = it.map { item ->
                            if (item.taxonomy == Taxonomy.COUPONS.type) {
                                item.copy(name = "${item.name} (coupons)")
                            } else {
                                item
                            }
                        }
                        filteringCategories.value = updatedItemList
                    }

                if (allDeals.isEmpty())
                    launchWithLoading {
                        interactor.getInitialDeals(currentCategoryType, id)
                            .onSuccess {
                                deals.postValue(it.toParcelableList())
                                allDeals.addAll(it.toParcelableList())
                            }
                            .onFailure {
                                error.postValue(it.toString())
                            }
                    }
            }
//            loadingGifVisibility.value = false
        }
    }

    fun getFilteringDeals() {
        if (filteringJob?.isActive == true) filteringJob?.cancel()

        filteringJob = viewModelScope.launchWithLoading {
            interactor.getSortingDeals(
                dealType = dealType.takeIf { it != DealType.ALL },
                sortBy = sortBy,
                categorySlug = categorySlug.takeIf { it.isNotEmpty() },
                shopSlug = shopSlug.takeIf { it.isNotEmpty() },
                taxonomy = filteringCategories.value?.getTaxonomyBySlug(categorySlug)
            )
                .onSuccess { _deals ->
                    if (_deals.isNotEmpty()) {
                        allDeals.clear()
                        allDeals.addAll(_deals.toParcelableList())
                        deals.postValue(allDeals as List<DealParcelable>)
                    }
                }
                .onFailure {
                    log(it.toString())
                    filteringError.postValue(it.toString())
                }
        }
    }

    fun loadMoreDeals() {
        if (isNeedMoreLoading) {
            if (filteringJob?.isActive == true) filteringJob?.cancel()

            filteringJob = viewModelScope.launchWithLoading {
                interactor.getSortingDeals(
                    page = currentPage.toString(),
                    dealType = dealType.takeIf { it != DealType.ALL },
                    sortBy = sortBy,
                    categorySlug = categorySlug.takeIf { it.isNotEmpty() },
                    shopSlug = shopSlug.takeIf { it.isNotEmpty() },
                    taxonomy = filteringCategories.value?.getTaxonomyBySlug(categorySlug)
                )
                    .onSuccess { _deals ->
                        if (_deals.isNotEmpty()) {
                            allDeals.addAll(_deals.toParcelableList())
                            deals.postValue(allDeals as List<DealParcelable>)
                            currentPage++
                        } else {
                            isNeedMoreLoading = false
                        }
                    }
                    .onFailure {
                        log(it.toString())
                        isNeedMoreLoading = false
                    }
            }
        }
    }

    fun searchDeals(searchText: String) {
        if (searchJob?.isActive == true) searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)

            launchWithLoading {
                val deals = interactor.searchDeals(searchText)
                searchResult.value = deals.toParcelableList()
            }
        }
    }

    fun updateBookmark(dealId: String) {
        getUserId()?.let { userId ->
            viewModelScope.launch {
                interactor.updateBookmark(userId, dealId)
            }
        }
    }

}