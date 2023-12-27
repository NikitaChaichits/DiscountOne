package com.digeltech.discountone.ui.bestdeals

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

const val ITEMS_ON_PAGE = "50"

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
        viewModelScope.launch {
            loadingGifVisibility.value = true
            dealsRepository.getBestDeals()
                .onSuccess {
                    allDeals.addAll(it.posts.toParcelableList())
                    deals.postValue(it.posts.toParcelableList())
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

    fun getFilteringDeals() {
        if (filteringJob?.isActive == true) filteringJob?.cancel()

        filteringJob = viewModelScope.launchWithLoading {
            dealsRepository.getSortingDeals(
                dealType = dealType.takeIf { it != DealType.ALL },
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

    fun loadMoreDeals() {
        if (currentShopSpinnerPosition == 0 && currentCategorySpinnerPosition == 0) {
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
//
//    fun loadCategoryDeals(spinnerPosition: Int) {
//        currentCategorySpinnerPosition = spinnerPosition
//        selectedCategoryId = if (spinnerPosition > 0)
//            filteringCategories.value?.get(spinnerPosition - 1)?.id ?: 0
//        else 0
//
//        viewModelScope.launch {
//            if (spinnerPosition == 0) {
//                if (currentShopSpinnerPosition == 0) {
//                    deals.postValue(allDeals)
//                } else {
//                    loadShopDeals(currentShopSpinnerPosition)
//                }
//            } else {
//                viewModelScope.launchWithLoading {
//                    filteringCategories.value?.get(spinnerPosition - 1)?.id?.let { categoryId ->
//                        dealsRepository.getDealsByCategoryAndShopId(
//                            categoryId = categoryId,
//                            shopId = selectedShopId.takeIf { shopId -> shopId != 0 })
//                            .onSuccess { _deals ->
//                                allDealsWithSorting.clear()
//                                allDealsWithSorting.addAll(_deals.toParcelableList())
//                                deals.postValue(allDealsWithSorting as List<DealParcelable>)
//                                currentPage = 3
//                            }
//                            .onFailure { e ->
//                                log(e.toString())
//                                error.postValue(e.toString())
//                            }
//                    }
//                }
//            }
//        }
//    }
//
//    fun loadShopDeals(spinnerPosition: Int) {
//        currentShopSpinnerPosition = spinnerPosition
//
//        selectedShopId = if (spinnerPosition > 0)
//            filteringShops.value?.get(spinnerPosition - 1)?.id ?: 0
//        else 0
//
//        viewModelScope.launch {
//            if (spinnerPosition == 0) {
//                if (currentCategorySpinnerPosition == 0) {
//                    deals.postValue(allDeals)
//                } else {
//                    loadCategoryDeals(currentCategorySpinnerPosition)
//                }
//            } else {
//                viewModelScope.launchWithLoading {
//                    filteringShops.value?.get(spinnerPosition - 1)?.id?.let { shopId ->
//                        dealsRepository.getDealsByCategoryAndShopId(
//                            categoryId = selectedCategoryId.takeIf { categoryId -> categoryId != 0 },
//                            shopId = shopId
//                        )
//                            .onSuccess { _deals ->
//                                allDealsWithSorting.clear()
//                                allDealsWithSorting.addAll(_deals.toParcelableList())
//                                deals.postValue(allDealsWithSorting as List<DealParcelable>)
//                                currentPage = 3
//                            }
//                            .onFailure { e ->
//                                log(e.toString())
//                                error.postValue(e.toString())
//                            }
//                    }
//                }
//            }
//        }
//    }
}