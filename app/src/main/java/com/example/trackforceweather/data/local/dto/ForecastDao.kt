package com.example.trackforceweather.data.local.dto

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.trackforceweather.data.local.entities.ForecastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {
    @Query("SELECT * FROM forecast ORDER BY timestamp")
    suspend fun getAllForecast(): List<ForecastEntity>

    @Query("SELECT * FROM forecast ORDER BY timestamp")
    fun getAllForecastFlow(): Flow<List<ForecastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: List<ForecastEntity>)

    @Query("DELETE FROM weather")
    suspend fun deleteAllForecast()
}