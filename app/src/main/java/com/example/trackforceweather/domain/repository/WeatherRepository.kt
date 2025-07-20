package com.example.trackforceweather.domain.repository

import com.example.trackforceweather.domain.model.LocationData
import com.example.trackforceweather.domain.model.Resource
import com.example.trackforceweather.domain.model.Weather
import com.example.trackforceweather.domain.model.WeatherForecast
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(location: LocationData): Flow<Resource<Weather>>
    suspend fun getWeatherForecast(location: LocationData): Flow<Resource<WeatherForecast>>
    fun getCachedWeatherFlow(): Flow<Weather?>
    fun getCachedForecastFlow(): Flow<WeatherForecast?>
}