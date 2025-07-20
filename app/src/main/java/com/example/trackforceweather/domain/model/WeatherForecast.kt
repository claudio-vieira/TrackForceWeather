package com.example.trackforceweather.domain.model

data class WeatherForecast(
    val dailyForecasts: List<DailyForecast>
)

data class DailyForecast(
    val timestamp: Long,
    val tempMin: Double,
    val tempMax: Double,
    val description: String,
    val iconCode: String
)