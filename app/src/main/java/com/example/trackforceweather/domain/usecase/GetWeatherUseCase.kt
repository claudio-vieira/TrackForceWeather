package com.example.trackforceweather.domain.usecase

import com.example.trackforceweather.domain.model.LocationData
import com.example.trackforceweather.domain.model.Resource
import com.example.trackforceweather.domain.model.Weather
import com.example.trackforceweather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend fun execute(location: LocationData): Flow<Resource<Weather>> {
        return repository.getCurrentWeather(location)
    }
}