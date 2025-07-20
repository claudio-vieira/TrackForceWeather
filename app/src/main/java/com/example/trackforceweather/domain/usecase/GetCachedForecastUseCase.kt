package com.example.trackforceweather.domain.usecase

import com.example.trackforceweather.domain.model.WeatherForecast
import com.example.trackforceweather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCachedForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    fun execute(): Flow<WeatherForecast?> {
        return repository.getCachedForecastFlow()
    }
}