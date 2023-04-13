package com.digeltech.appdiscountone.di

import com.digeltech.appdiscountone.domain.repository.CategoriesRepository
import com.digeltech.appdiscountone.domain.repository.ShopsRepository
import com.digeltech.appdiscountone.ui.categories.interactor.CategoriesInteractor
import com.digeltech.appdiscountone.ui.categories.interactor.CategoriesInteractorImpl
import com.digeltech.appdiscountone.ui.shops.interactor.ShopsInteractor
import com.digeltech.appdiscountone.ui.shops.interactor.ShopsInteractorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object InteractorModule {

    @Provides
    fun provideCategoriesInteractor(
        categoriesRepository: CategoriesRepository
    ): CategoriesInteractor =
        CategoriesInteractorImpl(categoriesRepository = categoriesRepository)

    @Provides
    fun provideShopsInteractor(
        shopsRepository: ShopsRepository
    ): ShopsInteractor =
        ShopsInteractorImpl(shopsRepository = shopsRepository)

}