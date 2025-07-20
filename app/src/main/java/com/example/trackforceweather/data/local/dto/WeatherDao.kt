package com.example.trackforceweather.data.local.dto

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.trackforceweather.data.local.entities.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestWeather(): WeatherEntity?

    @Query("SELECT * FROM weather ORDER BY timestamp DESC LIMIT 1")
    fun getLatestWeatherFlow(): Flow<WeatherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("DELETE FROM weather")
    suspend fun deleteWeather()
}