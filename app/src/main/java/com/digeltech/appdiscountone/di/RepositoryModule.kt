package com.digeltech.appdiscountone.di

import com.digeltech.appdiscountone.data.repository.CategoriesRepositoryImpl
import com.digeltech.appdiscountone.data.repository.DealsRepositoryImpl
import com.digeltech.appdiscountone.data.repository.ShopsRepositoryImpl
import com.digeltech.appdiscountone.data.source.remote.api.ServerApi
import com.digeltech.appdiscountone.domain.repository.CategoriesRepository
import com.digeltech.appdiscountone.domain.repository.DealsRepository
import com.digeltech.appdiscountone.domain.repository.ShopsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideCouponRepository(serverApi: ServerApi): DealsRepository =
        DealsRepositoryImpl(serverApi)

    @Provides
    fun provideCategoriesRepository(serverApi: ServerApi): CategoriesRepository =
        CategoriesRepositoryImpl(serverApi)

    @Provides
    fun provideShopsRepository(serverApi: ServerApi): ShopsRepository =
        ShopsRepositoryImpl(serverApi)
}