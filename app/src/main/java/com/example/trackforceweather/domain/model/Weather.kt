package com.example.trackforceweather.domain.model

data class Weather(
    val id: Long,
    val cityName: String,
    val description: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val iconCode: String,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double
)