package com.alcanl.cleverear.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Module
@InstallIn(SingletonComponent::class)
object ExecutorServiceModule {
    @Provides
    fun createExecutorService() : ExecutorService
    {
        return Executors.newFixedThreadPool(3)
    }
}