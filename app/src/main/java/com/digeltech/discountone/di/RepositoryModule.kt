package com.digeltech.discountone.di

import com.digeltech.discountone.data.repository.*
import com.digeltech.discountone.data.source.remote.api.AuthApi
import com.digeltech.discountone.data.source.remote.api.CouponsApi
import com.digeltech.discountone.data.source.remote.api.DiscountApi
import com.digeltech.discountone.data.source.remote.api.ServerApi
import com.digeltech.discountone.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideDealRepository(serverApi: ServerApi, discountApi: DiscountApi): DealsRepository =
        DealsRepositoryImpl(serverApi, discountApi)

    @Provides
    fun provideCategoriesRepository(serverApi: ServerApi): CategoriesRepository =
        CategoriesRepositoryImpl(serverApi)

    @Provides
    fun provideShopsRepository(serverApi: ServerApi): ShopsRepository =
        ShopsRepositoryImpl(serverApi)

    @Provides
    fun provideAuthRepository(authApi: AuthApi): AuthRepository =
        AuthRepositoryImpl(authApi)

    @Provides
    fun provideCouponRepository(couponsApi: CouponsApi): CouponsRepository =
        CouponsRepositoryImpl(couponsApi)
}