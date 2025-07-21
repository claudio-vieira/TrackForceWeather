package com.example.trackforceweather.domain.repository

import com.example.trackforceweather.BuildConfig
import com.example.trackforceweather.data.local.dto.ForecastDao
import com.example.trackforceweather.data.local.dto.WeatherDao
import com.example.trackforceweather.data.mapper.ForecastMapper
import com.example.trackforceweather.data.mapper.WeatherMapper
import com.example.trackforceweather.data.remote.api.WeatherApi
import com.example.trackforceweather.domain.model.LocationData
import com.example.trackforceweather.domain.model.Resource
import com.example.trackforceweather.domain.model.Weather
import com.example.trackforceweather.domain.model.WeatherForecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val weatherDao: WeatherDao,
    private val forecastDao: ForecastDao,
    private val networkMonitor: NetworkMonitor
) : WeatherRepository {

    override suspend fun getCurrentWeather(location: LocationData): Flow<Resource<Weather>> = flow {

        if (!networkMonitor.isConnected()) {
            emit(Resource.Error("Not connected to the internet"))
            return@flow
        }

        emit(Resource.Loading())

        try {
            val response = weatherApi.getCurrentWeather(
                latitude = location.latitude,
                longitude = location.longitude,
                apiKey = BuildConfig.WEATHER_API_KEY
            )

            if (response.isSuccessful && response.body() != null) {

                val weather = WeatherMapper.mapFromDto(response.body()!!)

                // Cache the new data
                weatherDao.deleteWeather()
                weatherDao.insertWeather(WeatherMapper.mapToEntity(weather))

                emit(Resource.Success(weather))
            } else {
                emit(Resource.Error("Failed to fetch weather data"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getWeatherForecast(location: LocationData): Flow<Resource<WeatherForecast>> = flow {

        if (!networkMonitor.isConnected()) {
            emit(Resource.Error("Not connected to the internet"))
            return@flow
        }

        emit(Resource.Loading())

        try {
            val response = weatherApi.getWeatherForecast(
                latitude = location.latitude,
                longitude = location.longitude,
                apiKey = BuildConfig.WEATHER_API_KEY
            )

            if (response.isSuccessful && response.body() != null) {
                val forecast = ForecastMapper.mapFromDto(response.body()!!)

                // Cache the new data
                forecastDao.deleteAllForecast()
                forecastDao.insertForecast(ForecastMapper.mapToEntity(forecast))

                emit(Resource.Success(forecast))
            } else {
                emit(Resource.Error("An unexpected error occurred"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }

    }.flowOn(Dispatchers.IO)

    override fun getCachedWeatherFlow(): Flow<Weather?> {
        return weatherDao.getLatestWeatherFlow().map { entity ->
            entity?.let { WeatherMapper.mapFromEntity(it) }
        }
    }

    override fun getCachedForecastFlow(): Flow<WeatherForecast?> {
        return forecastDao.getAllForecastFlow().map { list ->
            WeatherForecast(ForecastMapper.mapFromEntity(list).dailyForecasts)
        }
    }

}