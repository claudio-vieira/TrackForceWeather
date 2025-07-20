package com.example.trackforceweather.domain.model

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val cityName: String? = null
)