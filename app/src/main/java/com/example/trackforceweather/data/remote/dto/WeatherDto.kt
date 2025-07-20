package com.example.trackforceweather.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherResponseDto(
    val coord: CoordDto,
    val weather: List<WeatherDto>,
    val main: MainDto,
    val wind: WindDto,
    val name: String,
    val dt: Long
)

data class CoordDto(
    val lat: Double,
    val lon: Double
)

data class WeatherDto(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class MainDto(
    val temp: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Int
)

data class WindDto(
    val speed: Double
)