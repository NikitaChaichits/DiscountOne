package com.digeltech.discountone.ui.bestdeals

import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseFilteringViewModel
import com.digeltech.discountone.domain.model.Item
import com.digeltech.discountone.domain.model.getTaxonomyBySlug
import com.digeltech.discountone.domain.repository.DealsRepository
import com.digeltech.discountone.ui.common.*
import com.digeltech.discountone.ui.common.model.*
import com.digeltech.discountone.util.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DealsViewModel @Inject constructor(
    private val dealsRepository: DealsRepository
) : BaseFilteringViewModel() {

    private val allDeals = mutableListOf<DealParcelable>()
    private val allDealsWithSorting = mutableListOf<DealParcelable>()

    init {
        initDeals()
    }

    private fun initDeals() {
        viewModelScope.launchWithLoading {
            dealsRepository.getBestDeals()
                .onSuccess {
                    val categories = mutableListOf<Item>()
                    it.categories.forEach { category ->
                        categories.add(Item(category.id, category.name, category.slug, category.taxonomy, true))
                        category.child.forEach { childItem ->
                            categories.add(Item(childItem.id, childItem.name, childItem.slug, childItem.taxonomy))
                        }
                    }
                    val updatedCategoriesList = categories.map { item ->
                        if (item.taxonomy == Taxonomy.COUPONS.type) {
                            item.copy(name = "${item.name} (coupons)")
                        } else {
                            item
                        }
                    }
                    filteringCategories.postValue(updatedCategoriesList)
                    filteringShops.postValue(it.shops)

                    val parcelableList = it.posts.toParcelableList()
                    allDeals.addAll(parcelableList)
                    deals.postValue(parcelableList.take(INIT_COUNT_OF_DEALS))
                }
                .onFailure { error.postValue(it.toString()) }
        }
    }

    fun getFilteringDeals() {
        if (currentShopSpinnerPosition == 0 && currentCategorySpinnerPosition == 0 && currentDealTypeSpinnerPosition == 0) {
            deals.postValue(allDeals as List<DealParcelable>)
        } else {
            if (filteringJob?.isActive == true) filteringJob?.cancel()

            filteringJob = viewModelScope.launchWithLoading {
                dealsRepository.getSortingDeals(
                    dealType = dealType.takeIf { it != DealType.ALL },
                    sortBy = SortBy.MOST_POPULAR,
                    categorySlug = categorySlug.takeIf { it.isNotEmpty() },
                    shopSlug = shopSlug.takeIf { it.isNotEmpty() },
                    taxonomy = filteringCategories.value?.getTaxonomyBySlug(categorySlug)
                )
                    .onSuccess { _deals ->
                        if (_deals.isNotEmpty()) {
                            allDealsWithSorting.clear()
                            allDealsWithSorting.addAll(_deals.toParcelableList())
                            deals.postValue(allDealsWithSorting as List<DealParcelable>)
                        }
                    }
                    .onFailure {
                        log(it.toString())
                        filteringError.postValue(it.toString())
                    }
            }
        }
    }

    fun loadMoreDeals() {
        if ((deals.value?.size ?: 0) <= INIT_COUNT_OF_DEALS) {
            viewModelScope.launchWithLoading {
                delay(INIT_DELAY)
                deals.postValue(allDeals as List<DealParcelable>)
            }
            return
        }

        if (currentShopSpinnerPosition == 0 && currentCategorySpinnerPosition == 0 && currentDealTypeSpinnerPosition == 0) {
            viewModelScope.launchWithLoading {
                dealsRepository.getAllDeals(currentPage.toString(), ITEMS_ON_PAGE)
                    .onSuccess { _deals ->
                        if (_deals.isNotEmpty()) {
                            allDeals.addAll(_deals.toParcelableList())
                            deals.postValue(allDeals as List<DealParcelable>)
                            currentPage++
                        }
                    }
                    .onFailure {
                        log(it.toString())
                    }
            }
        } else {
            if (filteringJob?.isActive == true) filteringJob?.cancel()

            filteringJob = viewModelScope.launchWithLoading {
                dealsRepository.getSortingDeals(
                    page = currentPage.toString(),
                    dealType = dealType.takeIf { it != DealType.ALL },
                    sortBy = SortBy.MOST_POPULAR,
                    categorySlug = categorySlug.takeIf { it.isNotEmpty() },
                    shopSlug = shopSlug.takeIf { it.isNotEmpty() },
                    taxonomy = filteringCategories.value?.getTaxonomyBySlug(categorySlug)
                )
                    .onSuccess { _deals ->
                        if (_deals.isNotEmpty()) {
                            allDealsWithSorting.addAll(_deals.toParcelableList())
                            deals.postValue(allDealsWithSorting as List<DealParcelable>)
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
                val deals = dealsRepository.searchDeals(searchText)
                searchResult.value = deals.toParcelableList()
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