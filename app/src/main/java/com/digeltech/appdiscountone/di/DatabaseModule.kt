package com.digeltech.appdiscountone.di

import com.digeltech.appdiscountone.data.source.remote.DatabaseConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabaseConnection(): DatabaseConnection {
        return DatabaseConnection()
    }

}