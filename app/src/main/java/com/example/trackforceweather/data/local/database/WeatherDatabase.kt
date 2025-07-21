package com.example.trackforceweather.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.trackforceweather.data.local.dto.ForecastDao
import com.example.trackforceweather.data.local.dto.WeatherDao
import com.example.trackforceweather.data.local.entities.ForecastEntity
import com.example.trackforceweather.data.local.entities.WeatherEntity

@Database(
    entities = [WeatherEntity::class, ForecastEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun forecastDao(): ForecastDao
}
