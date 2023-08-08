package com.digeltech.discountone.di

import android.app.Application
import android.content.Context
import com.digeltech.discountone.data.file.FileManager
import com.digeltech.discountone.data.file.FileManagerImpl
import com.facebook.appevents.AppEventsLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideFileManager(context: Context): FileManager = FileManagerImpl(context)

    @Provides
    @Singleton
    fun provideAppEventsLogger(application: Application): AppEventsLogger {
        return AppEventsLogger.newLogger(application)
    }
}