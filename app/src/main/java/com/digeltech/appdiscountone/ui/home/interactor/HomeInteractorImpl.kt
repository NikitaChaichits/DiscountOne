package com.digeltech.appdiscountone.ui.home.interactor

import com.digeltech.appdiscountone.domain.model.CategoryWithDeals
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.repository.CategoriesRepository
import com.digeltech.appdiscountone.domain.repository.DealsRepository
import com.digeltech.appdiscountone.ui.home.adapter.Banner
import javax.inject.Inject

internal class HomeInteractorImpl @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
    private val dealsRepository: DealsRepository,
) : HomeInteractor {

    override suspend fun getBanners(): List<Banner> = dealsRepository.getBanners()

    override suspend fun getInitCategories(): List<CategoryWithDeals> = categoriesRepository.getInitHomeCategories()

    override suspend fun getAllCategories(): List<CategoryWithDeals> = categoriesRepository.getAllHomeCategories()

    override suspend fun getDeal(dealId: Int, categoryId: Int): Deal =
        dealsRepository.getDealById(dealId = dealId, categoryId = categoryId)

}