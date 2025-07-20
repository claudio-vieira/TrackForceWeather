package com.example.trackforceweather.domain.usecase

import app.cash.turbine.test
import com.example.trackforceweather.MainDispatcherRule
import com.example.trackforceweather.domain.model.DailyForecast
import com.example.trackforceweather.domain.model.LocationData
import com.example.trackforceweather.domain.model.Resource
import com.example.trackforceweather.domain.model.Weather
import com.example.trackforceweather.domain.model.WeatherForecast
import com.example.trackforceweather.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherUseCasesTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<WeatherRepository>()
    private lateinit var getWeatherUseCase: GetWeatherUseCase
    private lateinit var getWeatherForecastUseCase: GetWeatherForecastUseCase
    private lateinit var getCachedWeatherUseCase: GetCachedWeatherUseCase
    private lateinit var getCachedForecastUseCase: GetCachedForecastUseCase

    @Before
    fun setup() {
        getWeatherUseCase = GetWeatherUseCase(repository)
        getWeatherForecastUseCase = GetWeatherForecastUseCase(repository)
        getCachedWeatherUseCase = GetCachedWeatherUseCase(repository)
        getCachedForecastUseCase = GetCachedForecastUseCase(repository)
    }

    @Test
    fun `GetWeatherUseCase emits repository data`() = runTest {
        val location = LocationData(1.0, 2.0, "City")
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
        val flow = flowOf(Resource.Success(weather))
        coEvery { repository.getCurrentWeather(location) } returns flow

        getWeatherUseCase.execute(location).test {
            assertEquals(Resource.Success(weather), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `GetWeatherForecastUseCase emits repository data`() = runTest {
        val location = LocationData(1.0, 2.0, "City")
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
        val flow = flowOf(Resource.Success(forecast))
        coEvery { repository.getWeatherForecast(location) } returns flow

        getWeatherForecastUseCase.execute(location).test {
            assertEquals(Resource.Success(forecast), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `GetCachedWeatherUseCase emits repository data`() = runTest {
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
        val flow = flowOf(weather)
        every { repository.getCachedWeatherFlow() } returns flow

        getCachedWeatherUseCase.execute().test {
            assertEquals(weather, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `GetCachedForecastUseCase emits repository data`() = runTest {
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
        val flow = flowOf(forecast)
        every { repository.getCachedForecastFlow() } returns flow

        getCachedForecastUseCase.execute().test {
            assertEquals(forecast, awaitItem())
            awaitComplete()
        }
    }
}
