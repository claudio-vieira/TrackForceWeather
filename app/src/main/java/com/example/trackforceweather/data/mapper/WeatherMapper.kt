package com.example.trackforceweather.data.mapper

import com.example.trackforceweather.data.local.entities.WeatherEntity
import com.example.trackforceweather.data.remote.dto.WeatherResponseDto
import com.example.trackforceweather.domain.model.Weather

// Adapter pattern implementation
object WeatherMapper {

    fun mapFromDto(dto: WeatherResponseDto): Weather {
        return Weather(
            id = dto.weather.firstOrNull()?.id?.toLong() ?: 0L,
            cityName = dto.name,
            description = dto.weather.firstOrNull()?.description ?: "",
            temperature = dto.main.temp,
            feelsLike = dto.main.feelsLike,
            humidity = dto.main.humidity,
            pressure = dto.main.pressure,
            windSpeed = dto.wind.speed,
            iconCode = dto.weather.firstOrNull()?.icon ?: "",
            timestamp = dto.dt * 1000L, // Convert to milliseconds
            latitude = dto.coord.lat,
            longitude = dto.coord.lon
        )
    }

    fun mapToEntity(weather: Weather): WeatherEntity {
        return WeatherEntity(
            id = weather.id,
            cityName = weather.cityName,
            description = weather.description,
            temperature = weather.temperature,
            feelsLike = weather.feelsLike,
            humidity = weather.humidity,
            pressure = weather.pressure,
            windSpeed = weather.windSpeed,
            iconCode = weather.iconCode,
            timestamp = weather.timestamp,
            latitude = weather.latitude,
            longitude = weather.longitude
        )
    }

    fun mapFromEntity(entity: WeatherEntity): Weather {
        return Weather(
            id = entity.id,
            cityName = entity.cityName,
            description = entity.description,
            temperature = entity.temperature,
            feelsLike = entity.feelsLike,
            humidity = entity.humidity,
            pressure = entity.pressure,
            windSpeed = entity.windSpeed,
            iconCode = entity.iconCode,
            timestamp = entity.timestamp,
            latitude = entity.latitude,
            longitude = entity.longitude
        )
    }
}