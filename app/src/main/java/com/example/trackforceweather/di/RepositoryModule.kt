package com.example.trackforceweather.di

import com.example.trackforceweather.domain.repository.NetworkMonitor
import com.example.trackforceweather.domain.repository.NetworkMonitorImpl
import com.example.trackforceweather.domain.repository.WeatherRepository
import com.example.trackforceweather.domain.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        networkMonitorImpl: NetworkMonitorImpl
    ): NetworkMonitor
}