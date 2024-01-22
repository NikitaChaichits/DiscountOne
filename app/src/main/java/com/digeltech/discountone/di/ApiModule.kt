package com.digeltech.discountone.di

import com.digeltech.discountone.data.constants.RemoteConstants.BASE_URL
import com.digeltech.discountone.data.source.remote.api.AuthApi
import com.digeltech.discountone.data.source.remote.api.CouponsApi
import com.digeltech.discountone.data.source.remote.api.DealsApi
import com.digeltech.discountone.data.source.remote.api.DiscountsApi
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS) // connection timeout to 5 seconds
            .readTimeout(25, TimeUnit.SECONDS) // read timeout to 20 seconds
            .build()
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideServerApi(retrofit: Retrofit): DealsApi =
        retrofit.create(DealsApi::class.java)

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideDiscountApi(retrofit: Retrofit): DiscountsApi =
        retrofit.create(DiscountsApi::class.java)

    @Provides
    @Singleton
    fun provideCouponApi(retrofit: Retrofit): CouponsApi =
        retrofit.create(CouponsApi::class.java)
}
