package com.example.trackforceweather.presentation.viewmodel

import com.example.trackforceweather.domain.model.Weather
import com.example.trackforceweather.domain.model.WeatherForecast

data class WeatherUiState(
    val weather: Weather? = null,
    val forecast: WeatherForecast? = null,
    val isLoading: Boolean = false,
    val isForecastLoading: Boolean = false,
    val error: String? = null,
    val forecastError: String? = null
)