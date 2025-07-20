package com.example.trackforceweather.domain.usecase

import com.example.trackforceweather.domain.model.Weather
import com.example.trackforceweather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCachedWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    fun execute(): Flow<Weather?> {
        return repository.getCachedWeatherFlow()
    }
}