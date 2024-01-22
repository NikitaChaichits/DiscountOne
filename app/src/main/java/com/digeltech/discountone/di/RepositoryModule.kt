package com.digeltech.discountone.di

import com.digeltech.discountone.data.repository.*
import com.digeltech.discountone.data.source.remote.api.AuthApi
import com.digeltech.discountone.data.source.remote.api.CouponsApi
import com.digeltech.discountone.data.source.remote.api.DealsApi
import com.digeltech.discountone.data.source.remote.api.DiscountsApi
import com.digeltech.discountone.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideDealRepository(serverApi: DealsApi, discountApi: DiscountsApi): DealsRepository =
        DealsRepositoryImpl(serverApi, discountApi)

    @Provides
    fun provideCategoriesRepository(serverApi: DealsApi): CategoriesRepository =
        CategoriesRepositoryImpl(serverApi)

    @Provides
    fun provideShopsRepository(serverApi: DealsApi): ShopsRepository =
        ShopsRepositoryImpl(serverApi)

    @Provides
    fun provideAuthRepository(authApi: AuthApi): AuthRepository =
        AuthRepositoryImpl(authApi)

    @Provides
    fun provideCouponRepository(couponsApi: CouponsApi): CouponsRepository =
        CouponsRepositoryImpl(couponsApi)
}