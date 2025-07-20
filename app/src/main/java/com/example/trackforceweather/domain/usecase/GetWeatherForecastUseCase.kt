package com.example.trackforceweather.domain.usecase

import com.example.trackforceweather.domain.model.LocationData
import com.example.trackforceweather.domain.model.Resource
import com.example.trackforceweather.domain.model.WeatherForecast
import com.example.trackforceweather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend fun execute(location: LocationData): Flow<Resource<WeatherForecast>> {
        return repository.getWeatherForecast(location)
    }
}