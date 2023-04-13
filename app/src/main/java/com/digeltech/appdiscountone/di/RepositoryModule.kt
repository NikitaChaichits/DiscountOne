package com.digeltech.appdiscountone.di

import com.digeltech.appdiscountone.data.repository.CategoriesRepositoryImpl
import com.digeltech.appdiscountone.data.repository.CouponRepositoryImpl
import com.digeltech.appdiscountone.data.repository.ShopsRepositoryImpl
import com.digeltech.appdiscountone.data.source.remote.DatabaseConnection
import com.digeltech.appdiscountone.data.source.remote.RemoteDataSource
import com.digeltech.appdiscountone.domain.repository.CategoriesRepository
import com.digeltech.appdiscountone.domain.repository.CouponRepository
import com.digeltech.appdiscountone.domain.repository.ShopsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideCouponRepository(remoteDataSource: RemoteDataSource): CouponRepository =
        CouponRepositoryImpl(remoteDataSource)

    @Provides
    fun provideCategoriesRepository(databaseConnection: DatabaseConnection): CategoriesRepository =
        CategoriesRepositoryImpl(databaseConnection)

    @Provides
    fun provideShopsRepository(databaseConnection: DatabaseConnection): ShopsRepository =
        ShopsRepositoryImpl(databaseConnection)
}