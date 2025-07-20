package com.example.trackforceweather.viewmodel

import com.example.trackforceweather.MainDispatcherRule
import com.example.trackforceweather.domain.model.DailyForecast
import com.example.trackforceweather.domain.model.LocationData
import com.example.trackforceweather.domain.model.Resource
import com.example.trackforceweather.domain.model.Weather
import com.example.trackforceweather.domain.model.WeatherForecast
import com.example.trackforceweather.domain.usecase.GetCachedForecastUseCase
import com.example.trackforceweather.domain.usecase.GetCachedWeatherUseCase
import com.example.trackforceweather.domain.usecase.GetWeatherForecastUseCase
import com.example.trackforceweather.domain.usecase.GetWeatherUseCase
import com.example.trackforceweather.presentation.viewmodel.WeatherViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule() // Ensures Main = TestDispatcher

    // Mocks
    private val getWeatherUseCase: GetWeatherUseCase = mockk()
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase = mockk()
    private val getCachedWeatherUseCase: GetCachedWeatherUseCase = mockk()
    private val getCachedForecastUseCase: GetCachedForecastUseCase = mockk()

    private lateinit var viewModel: WeatherViewModel

    private val location = LocationData(0.0, 0.0, "Test City")
    private val weather = Weather(
        id = 1L,
        cityName = "Test City",
        description = "Clear sky",
        temperature = 25.0,
        feelsLike = 26.0,
        humidity = 60,
        pressure = 1012,
        windSpeed = 5.0,
        iconCode = "01d",
        timestamp = System.currentTimeMillis(),
        latitude = 0.0,
        longitude = 0.0
    )
    private val forecast = WeatherForecast(
        dailyForecasts = listOf(
            DailyForecast(
                timestamp = System.currentTimeMillis(),
                tempMin = 20.0,
                tempMax = 28.0,
                description = "Sunny",
                iconCode = "01d"
            )
        )
    )

    @Before
    fun setup() {
        // Default no-op for cached flows
        coEvery { getCachedWeatherUseCase.execute() } returns flowOf()
        coEvery { getCachedForecastUseCase.execute() } returns flowOf()

        viewModel = WeatherViewModel(
            getWeatherUseCase,
            getWeatherForecastUseCase,
            getCachedWeatherUseCase,
            getCachedForecastUseCase
        )
    }

    @Test
    fun `loadWeather updates state with weather on success`() = runTest {
        coEvery { getWeatherUseCase.execute(location) } returns flowOf(
            Resource.Loading(),
            Resource.Success(weather)
        )

        viewModel.loadWeather(location)

        val finalState = viewModel.uiState.first { it.weather != null }

        assertFalse(finalState.isLoading)
        assertEquals(weather, finalState.weather)
        assertNull(finalState.error)
    }

    @Test
    fun `loadWeather updates state with error on failure`() = runTest {
        val errorMessage = "Network error"
        coEvery { getWeatherUseCase.execute(location) } returns flowOf(
            Resource.Loading(),
            Resource.Error(errorMessage)
        )

        viewModel.loadWeather(location)

        val finalState = viewModel.uiState.first { it.error != null }

        assertFalse(finalState.isLoading)
        assertEquals(errorMessage, finalState.error)
    }

    @Test
    fun `loadWeatherForecast updates state with forecast on success`() = runTest {
        coEvery { getWeatherForecastUseCase.execute(location) } returns flowOf(
            Resource.Loading(),
            Resource.Success(forecast)
        )

        viewModel.loadWeatherForecast(location)

        val finalState = viewModel.uiState.first { it.forecast != null }

        assertFalse(finalState.isForecastLoading)
        assertEquals(forecast, finalState.forecast)
        assertNull(finalState.forecastError)
    }

    @Test
    fun `loadWeatherForecast updates state with error on failure`() = runTest {
        val errorMessage = "Server error"
        coEvery { getWeatherForecastUseCase.execute(location) } returns flowOf(
            Resource.Loading(),
            Resource.Error(errorMessage)
        )

        viewModel.loadWeatherForecast(location)

        val finalState = viewModel.uiState.first { it.forecastError != null }

        assertFalse(finalState.isForecastLoading)
        assertEquals(errorMessage, finalState.forecastError)
    }

    @Test
    fun `clearError clears the error in the state`() = runTest {
        val errorMessage = "Error occurred"
        coEvery { getWeatherUseCase.execute(location) } returns flowOf(
            Resource.Loading(),
            Resource.Error(errorMessage)
        )

        viewModel.loadWeather(location)

        val errorState = viewModel.uiState.first { it.error != null }
        assertEquals(errorMessage, errorState.error)

        viewModel.clearError()

        val clearedState = viewModel.uiState.value
        assertNull(clearedState.error)
    }

    @Test
    fun `clearForecastError clears the forecast error in the state`() = runTest {
        val errorMessage = "Forecast error"
        coEvery { getWeatherForecastUseCase.execute(location) } returns flowOf(
            Resource.Loading(),
            Resource.Error(errorMessage)
        )

        viewModel.loadWeatherForecast(location)

        val errorState = viewModel.uiState.first { it.forecastError != null }
        assertEquals(errorMessage, errorState.forecastError)

        viewModel.clearForecastError()

        val clearedState = viewModel.uiState.value
        assertNull(clearedState.forecastError)
    }
}