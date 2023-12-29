package com.alcanl.cleverear.di

import com.alcanl.cleverear.helpers.Configuration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
@Module
@InstallIn(SingletonComponent::class)
object ConfigurationModule {
    @Provides
    fun createConfiguration() : Configuration = Configuration()
}