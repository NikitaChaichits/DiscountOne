package com.digeltech.discountone.ui.discounts

import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseFilteringViewModel
import com.digeltech.discountone.domain.model.Item
import com.digeltech.discountone.domain.model.getTaxonomyBySlug
import com.digeltech.discountone.domain.repository.DealsRepository
import com.digeltech.discountone.ui.common.SEARCH_DELAY
import com.digeltech.discountone.ui.common.getUserId
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.ui.common.model.DealType
import com.digeltech.discountone.ui.common.model.toParcelableList
import com.digeltech.discountone.util.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscountsViewModel @Inject constructor(
    private val dealsRepository: DealsRepository
) : BaseFilteringViewModel() {

    private val allDeals = mutableListOf<DealParcelable>()

    fun initDeals(categorySlug: String?) {
        if (allDeals.isEmpty()) {
            viewModelScope.launch {
                loadingGifVisibility.value = true
                dealsRepository.getDiscounts()
                    .onSuccess {
                        if (categorySlug.isNullOrEmpty()) {
                            allDeals.addAll(it.posts.toParcelableList())
                            deals.postValue(it.posts.toParcelableList())
                        } else {
                            this@DiscountsViewModel.categorySlug = categorySlug
                        }

                        filteringShops.postValue(it.shops)

                        val categories = mutableListOf<Item>()
                        it.categories.forEach { category ->
                            categories.add(Item(category.id, category.name, category.slug, category.taxonomy, true))
                            category.child.forEach { childItem ->
                                categories.add(Item(childItem.id, childItem.name, childItem.slug, childItem.taxonomy))
                            }
                        }
                        val updatedCategoriesList = categories.map { item ->
                            if (item.taxonomy == "categories-coupons") {
                                item.copy(name = "${item.name} (coupon)")
                            } else {
                                item
                            }
                        }
                        filteringCategories.postValue(updatedCategoriesList)
                    }
                    .onFailure { error.postValue(it.toString()) }
                loadingGifVisibility.value = false
            }
        }
    }

    fun getFilteringDeals() {
        if (filteringJob?.isActive == true) filteringJob?.cancel()

        filteringJob = viewModelScope.launchWithLoading {
            dealsRepository.getSortingDeals(
                dealType = DealType.DISCOUNTS,
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
                dealsRepository.getSortingDeals(
                    page = currentPage.toString(),
                    dealType = DealType.DISCOUNTS,
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