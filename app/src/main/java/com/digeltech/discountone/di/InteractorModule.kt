package com.digeltech.discountone.di

import com.digeltech.discountone.domain.repository.CategoriesRepository
import com.digeltech.discountone.domain.repository.DealsRepository
import com.digeltech.discountone.domain.repository.ShopsRepository
import com.digeltech.discountone.ui.categories.interactor.CategoriesInteractor
import com.digeltech.discountone.ui.categories.interactor.CategoriesInteractorImpl
import com.digeltech.discountone.ui.categoryandshop.interactor.CategoryAndShopInteractor
import com.digeltech.discountone.ui.categoryandshop.interactor.CategoryAndShopInteractorImpl
import com.digeltech.discountone.ui.home.interactor.HomeInteractor
import com.digeltech.discountone.ui.home.interactor.HomeInteractorImpl
import com.digeltech.discountone.ui.shops.interactor.ShopsInteractor
import com.digeltech.discountone.ui.shops.interactor.ShopsInteractorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object InteractorModule {

    @Provides
    fun provideCategoriesInteractor(
        categoriesRepository: CategoriesRepository,
        dealsRepository: DealsRepository
    ): CategoriesInteractor =
        CategoriesInteractorImpl(
            categoriesRepository = categoriesRepository,
            dealsRepository = dealsRepository
        )

    @Provides
    fun provideShopsInteractor(
        shopsRepository: ShopsRepository,
        dealsRepository: DealsRepository
    ): ShopsInteractor =
        ShopsInteractorImpl(
            shopsRepository = shopsRepository,
            dealsRepository = dealsRepository
        )

    @Provides
    fun provideHomeInteractor(
        dealsRepository: DealsRepository,
        categoriesRepository: CategoriesRepository,
    ): HomeInteractor =
        HomeInteractorImpl(
            dealsRepository = dealsRepository,
            categoriesRepository = categoriesRepository
        )

    @Provides
    fun provideCategoryAndShopInteractor(
        dealsRepository: DealsRepository,
        shopsRepository: ShopsRepository,
        categoriesRepository: CategoriesRepository,
    ): CategoryAndShopInteractor =
        CategoryAndShopInteractorImpl(
            dealsRepository = dealsRepository,
            shopsRepository = shopsRepository,
            categoriesRepository = categoriesRepository
        )

}