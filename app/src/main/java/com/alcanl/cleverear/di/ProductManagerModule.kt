package com.alcanl.cleverear.di

import com.ark.ProductManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductManagerModule {
    @Provides
    @Singleton
    fun createProductManager() : ProductManager = ProductManager.getInstance()
}