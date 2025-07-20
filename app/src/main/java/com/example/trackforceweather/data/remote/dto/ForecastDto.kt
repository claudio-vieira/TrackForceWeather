package com.example.trackforceweather.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ForecastResponseDto(
    val list: List<ForecastItemDto>
)

data class ForecastItemDto(
    val dt: Long,
    @SerializedName("dt_txt")
    val dateText: String,
    val main: MainDto,
    val weather: List<WeatherDto>
)