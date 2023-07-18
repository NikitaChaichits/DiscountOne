package com.digeltech.discountone.di

import com.digeltech.discountone.data.repository.AuthRepositoryImpl
import com.digeltech.discountone.data.repository.CategoriesRepositoryImpl
import com.digeltech.discountone.data.repository.DealsRepositoryImpl
import com.digeltech.discountone.data.repository.ShopsRepositoryImpl
import com.digeltech.discountone.data.source.remote.api.AuthApi
import com.digeltech.discountone.data.source.remote.api.ServerApi
import com.digeltech.discountone.domain.repository.AuthRepository
import com.digeltech.discountone.domain.repository.CategoriesRepository
import com.digeltech.discountone.domain.repository.DealsRepository
import com.digeltech.discountone.domain.repository.ShopsRepository
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

    @Provides
    fun provideAuthRepository(authApi: AuthApi): AuthRepository =
        AuthRepositoryImpl(authApi)
}