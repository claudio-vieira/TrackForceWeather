package com.example.trackforceweather.domain.repository

import app.cash.turbine.test
import com.example.trackforceweather.MainDispatcherRule
import com.example.trackforceweather.data.local.dto.ForecastDao
import com.example.trackforceweather.data.local.dto.WeatherDao
import com.example.trackforceweather.data.local.entities.ForecastEntity
import com.example.trackforceweather.data.local.entities.WeatherEntity
import com.example.trackforceweather.data.mapper.ForecastMapper
import com.example.trackforceweather.data.mapper.WeatherMapper
import com.example.trackforceweather.data.remote.api.WeatherApi
import com.example.trackforceweather.data.remote.dto.WeatherResponseDto
import com.example.trackforceweather.domain.model.DailyForecast
import com.example.trackforceweather.domain.model.LocationData
import com.example.trackforceweather.domain.model.Resource
import com.example.trackforceweather.domain.model.Weather
import com.example.trackforceweather.domain.model.WeatherForecast
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val weatherApi = mockk<WeatherApi>()
    private val weatherDao = mockk<WeatherDao>(relaxed = true)
    private val forecastDao = mockk<ForecastDao>(relaxed = true)
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setup() {
        repository = WeatherRepositoryImpl(weatherApi, weatherDao, forecastDao)
    }

    @Test
    fun `getCurrentWeather emits Loading and Success when API succeeds`() = runTest {
        val location = LocationData(1.0, 2.0, "City")
        val weatherResponseDto = mockk< WeatherResponseDto>()
        val weather = Weather(
            id = 1L,
            cityName = "City",
            description = "Clear",
            temperature = 25.0,
            feelsLike = 24.0,
            humidity = 50,
            pressure = 1013,
            windSpeed = 5.0,
            iconCode = "01d",
            timestamp = System.currentTimeMillis(),
            latitude = 1.0,
            longitude = 2.0
        )

        val response = Response.success(weatherResponseDto)
        coEvery { weatherApi.getCurrentWeather(any(), any(), any()) } returns response
        coEvery { weatherDao.deleteWeather() } just Runs
        coEvery { weatherDao.insertWeather(any()) } just Runs
        mockkObject(WeatherMapper)
        every { WeatherMapper.mapFromDto(weatherResponseDto) } returns weather
        every { WeatherMapper.mapToEntity(weather) } returns mockk()

        repository.getCurrentWeather(location).test {
            assert(awaitItem() is Resource.Loading)
            val success = awaitItem()
            assert(success is Resource.Success && success.data == weather)
            awaitComplete()
        }
    }

    @Test
    fun `getCurrentWeather emits Loading and Error when API fails`() = runTest {
        val location = LocationData(1.0, 2.0, "City")
        val response: Response<WeatherResponseDto> = Response.error(400, "".toResponseBody())
        coEvery { weatherApi.getCurrentWeather(any(), any(), any()) } returns response

        repository.getCurrentWeather(location).test {
            assert(awaitItem() is Resource.Loading)
            val error = awaitItem()
            assert(error is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `getCurrentWeather emits Loading and Error on exception`() = runTest {
        val location = LocationData(1.0, 2.0, "City")
        coEvery { weatherApi.getCurrentWeather(any(), any(), any()) } throws RuntimeException("Network failure")

        repository.getCurrentWeather(location).test {
            assert(awaitItem() is Resource.Loading)
            val error = awaitItem()
            assert(error is Resource.Error && error.message.contains("Network failure"))
            awaitComplete()
        }
    }

    @Test
    fun `getCachedWeatherFlow emits mapped weather`() = runTest {
        val weatherEntity = mockk<WeatherEntity>()
        val weather = mockk<Weather>()
        every { weatherDao.getLatestWeatherFlow() } returns flowOf(weatherEntity)
        mockkObject(WeatherMapper)
        every { WeatherMapper.mapFromEntity(weatherEntity) } returns weather

        repository.getCachedWeatherFlow().test {
            assertEquals(weather, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getCachedForecastFlow emits mapped forecast`() = runTest {
        val forecastEntities = listOf(mockk<ForecastEntity>())
        val forecast = WeatherForecast(
            dailyForecasts = listOf(
                DailyForecast(
                    timestamp = System.currentTimeMillis(),
                    tempMin = 18.0,
                    tempMax = 28.0,
                    description = "Sunny",
                    iconCode = "01d"
                )
            )
        )
        every { forecastDao.getAllForecastFlow() } returns flowOf(forecastEntities)
        mockkObject(ForecastMapper)
        every { ForecastMapper.mapFromEntity(forecastEntities) } returns forecast

        repository.getCachedForecastFlow().test {
            assertEquals(forecast, awaitItem())
            awaitComplete()
        }
    }
}
