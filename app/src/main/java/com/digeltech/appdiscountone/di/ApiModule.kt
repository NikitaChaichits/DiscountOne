package com.digeltech.appdiscountone.di

import com.digeltech.appdiscountone.data.constants.RemoteConstants.BASE_URL
import com.digeltech.appdiscountone.data.source.remote.api.AuthApi
import com.digeltech.appdiscountone.data.source.remote.api.ServerApi
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
            .readTimeout(20, TimeUnit.SECONDS) // read timeout to 20 seconds
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
    fun provideServerApi(retrofit: Retrofit): ServerApi =
        retrofit.create(ServerApi::class.java)

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)
}
