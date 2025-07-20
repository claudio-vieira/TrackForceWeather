package com.example.trackforceweather.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * As the API returns a list of weather forecasts for the next 5 days with 3 hourly intervals,
 * We can use the dt property as the primary key to uniquely identify each forecast item.
 * */
@Entity(tableName = "forecast")
data class ForecastEntity(
    @PrimaryKey val timestamp: Long,
    val tempMin: Double,
    val tempMax: Double,
    val description: String,
    val iconCode: String
)
