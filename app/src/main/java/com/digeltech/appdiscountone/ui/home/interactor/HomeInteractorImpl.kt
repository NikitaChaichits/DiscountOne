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

    override suspend fun getCategories(): List<CategoryWithDeals> = categoriesRepository.getHomeCategories()

    override suspend fun getDeal(dealId: Int): Deal = dealsRepository.getDealById(dealId)

}