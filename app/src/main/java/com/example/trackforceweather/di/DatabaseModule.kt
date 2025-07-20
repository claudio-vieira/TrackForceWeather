package com.example.trackforceweather.di

import android.content.Context
import androidx.room.Room
import com.example.trackforceweather.data.local.database.WeatherDatabase
import com.example.trackforceweather.data.local.dto.ForecastDao
import com.example.trackforceweather.data.local.dto.WeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWeatherDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            WeatherDatabase::class.java,
            "track_force_weather_database"
        ).build()
    }

    @Provides
    fun provideWeatherDao(database: WeatherDatabase): WeatherDao {
        return database.weatherDao()
    }

    @Provides
    fun provideForecastDao(database: WeatherDatabase): ForecastDao {
        return database.forecastDao()
    }
}