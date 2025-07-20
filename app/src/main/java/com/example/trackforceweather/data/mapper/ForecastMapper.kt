package com.example.trackforceweather.data.mapper

import com.example.trackforceweather.data.local.entities.ForecastEntity
import com.example.trackforceweather.data.remote.dto.ForecastResponseDto
import com.example.trackforceweather.domain.model.DailyForecast
import com.example.trackforceweather.domain.model.WeatherForecast
import java.time.Instant
import java.time.ZoneId

// Adapter pattern implementation
object ForecastMapper {
    /**
     * It's grouping by day to show only the min and max temperature for each day.
     * */
    fun mapFromDto(dto: ForecastResponseDto): WeatherForecast {
        val grouped = dto.list.groupBy { item ->
            // Convert dt (seconds) to LocalDate using system default timezone
            Instant.ofEpochSecond(item.dt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }

        val dailyForecasts = grouped.map { (localDate, forecastItems) ->
            val tempMin = forecastItems.minOf { it.main.tempMin }
            val tempMax = forecastItems.maxOf { it.main.tempMax }

            val firstItem = forecastItems.first()
            val description = firstItem.weather.firstOrNull()?.description.orEmpty()
            val iconCode = firstItem.weather.firstOrNull()?.icon.orEmpty()

            //Save the timestamp as the start of the day in your timezone
            val timestamp = localDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()

            DailyForecast(
                timestamp = timestamp,
                tempMin = tempMin,
                tempMax = tempMax,
                description = description,
                iconCode = iconCode
            )
        }

        return WeatherForecast(dailyForecasts = dailyForecasts)
    }

    fun mapToEntity(list: WeatherForecast): List<ForecastEntity> {
        return list.dailyForecasts.map { entity ->
            ForecastEntity(
                timestamp = entity.timestamp,
                tempMin = entity.tempMin,
                tempMax = entity.tempMax,
                description = entity.description,
                iconCode = entity.iconCode
            )
        }

    }

    fun mapFromEntity(list: List<ForecastEntity>): WeatherForecast {
        return WeatherForecast(
            list.map { entity ->
                DailyForecast(
                    timestamp = entity.timestamp,
                    tempMin = entity.tempMin,
                    tempMax = entity.tempMax,
                    description = entity.description,
                    iconCode = entity.iconCode
                )
            }
        )
    }
}