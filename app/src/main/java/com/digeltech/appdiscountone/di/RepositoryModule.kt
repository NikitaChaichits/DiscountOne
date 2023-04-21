package com.digeltech.appdiscountone.di

import com.digeltech.appdiscountone.data.repository.CategoriesRepositoryImpl
import com.digeltech.appdiscountone.data.repository.DealsRepositoryImpl
import com.digeltech.appdiscountone.data.repository.ShopsRepositoryImpl
import com.digeltech.appdiscountone.data.source.remote.DatabaseConnection
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
    fun provideCouponRepository(databaseConnection: DatabaseConnection): DealsRepository =
        DealsRepositoryImpl(databaseConnection)

    @Provides
    fun provideCategoriesRepository(databaseConnection: DatabaseConnection): CategoriesRepository =
        CategoriesRepositoryImpl(databaseConnection)

    @Provides
    fun provideShopsRepository(databaseConnection: DatabaseConnection): ShopsRepository =
        ShopsRepositoryImpl(databaseConnection)
}