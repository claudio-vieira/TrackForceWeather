package com.example.trackforceweather.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        // Singleton pattern implementation
        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
