package com.example.trackforceweather.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val id: Long = 0,
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