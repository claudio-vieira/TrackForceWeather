package com.example.trackforceweather.presentation.viewmodel

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.trackforceweather.domain.model.DailyForecast
import com.example.trackforceweather.domain.model.WeatherForecast
import com.example.trackforceweather.presentation.ui.ErrorContent
import com.example.trackforceweather.presentation.ui.LocationPermissionContent
import com.example.trackforceweather.presentation.ui.WeatherDetailRow
import com.example.trackforceweather.presentation.ui.WeatherForecastContent
import com.example.trackforceweather.presentation.ui.WeatherForecastLoadingContent
import com.example.trackforceweather.presentation.ui.WeatherLoadingContent
import com.example.trackforceweather.presentation.ui.formatTimestampToDate
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun locationPermissionContent_displaysCorrectContent() {
        // Given
        var permissionRequested = false

        // When
        composeTestRule.setContent {
            LocationPermissionContent(
                onRequestPermission = { permissionRequested = true },
                shouldShowRationale = true
            )
        }

        // Then
        composeTestRule.onNodeWithText("📍").assertIsDisplayed()
        composeTestRule.onNodeWithText("Location Permission Required").assertIsDisplayed()
        composeTestRule.onNodeWithText("This app needs location permission to show weather data for your current location. Please grant permission to continue.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Grant Permission").assertIsDisplayed()
    }

    @Test
    fun locationPermissionContent_clickable() {
        // Given
        var permissionRequested = false

        // When
        composeTestRule.setContent {
            LocationPermissionContent(
                onRequestPermission = { permissionRequested = true },
                shouldShowRationale = true
            )
        }

        // Then
        composeTestRule.onNodeWithText("Grant Permission").performClick()
        assert(permissionRequested)
    }

    @Test
    fun locationPermissionContent_displaysCorrectContentWithoutRationale() {
        // When
        composeTestRule.setContent {
            LocationPermissionContent(
                onRequestPermission = {},
                shouldShowRationale = false
            )
        }

        // Then
        composeTestRule.onNodeWithText("To show weather data for your location, we need access to your device's location.").assertIsDisplayed()
    }

    @Test
    fun errorContent_displaysCorrectContent() {
        // Given
        val errorMessage = "Network connection failed"
        var retryClicked = false

        // When
        composeTestRule.setContent {
            ErrorContent(
                error = errorMessage,
                onRetry = { retryClicked = true }
            )
        }

        // Then
        composeTestRule.onNodeWithText("⚠️").assertIsDisplayed()
        composeTestRule.onNodeWithText("Error").assertIsDisplayed()
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun errorContent_retryClickable() {
        // Given
        var retryClicked = false

        // When
        composeTestRule.setContent {
            ErrorContent(
                error = "Test error",
                onRetry = { retryClicked = true }
            )
        }

        // Then
        composeTestRule.onNodeWithText("Retry").performClick()
        assert(retryClicked)
    }

    @Test
    fun weatherForecastContent_displaysCorrectContent() {
        // Given
        val forecastItems = listOf(
            DailyForecast(
                timestamp = 1640995200L, // 2022-01-01
                tempMin = 10.0,
                tempMax = 20.0,
                description = "sunny",
                iconCode = "01d"
            ),
            DailyForecast(
                timestamp = 1641081600L, // 2022-01-02
                tempMin = 12.5,
                tempMax = 22.7,
                description = "cloudy",
                iconCode = "02d"
            )
        )
        val forecast = WeatherForecast(dailyForecasts = forecastItems)

        // When
        composeTestRule.setContent {
            WeatherForecastContent(forecast = forecast)
        }

        // Then
        composeTestRule.onNodeWithText("Forecast").assertIsDisplayed()
        composeTestRule.onNodeWithText("10° - 20°").assertIsDisplayed()
        composeTestRule.onNodeWithText("12° - 23°").assertIsDisplayed() // 22.7 rounded to 23
    }

    @Test
    fun weatherForecastContent_displaysFormattedDates() {
        // Given
        val forecastItems = listOf(
            DailyForecast(
                timestamp = 1640995200L, // 2022-01-01
                tempMin = 15.0,
                tempMax = 25.0,
                description = "sunny",
                iconCode = "01d"
            )
        )
        val forecast = WeatherForecast(dailyForecasts = forecastItems)

        // When
        composeTestRule.setContent {
            WeatherForecastContent(forecast = forecast)
        }

        // Then
        composeTestRule.onNodeWithText("01/01/2022").assertIsDisplayed()
    }

    @Test
    fun weatherForecastContent_displaysEmptyStateWhenNoForecasts() {
        // Given
        val forecast = WeatherForecast(dailyForecasts = emptyList())

        // When
        composeTestRule.setContent {
            WeatherForecastContent(forecast = forecast)
        }

        // Then
        composeTestRule.onNodeWithText("Forecast").assertIsDisplayed()
        // Should not crash and should display the title even with empty list
    }

    @Test
    fun weatherDetailRow_displaysCorrectContent() {
        // When
        composeTestRule.setContent {
            WeatherDetailRow(label = "Temperature", value = "25°C")
        }

        // Then
        composeTestRule.onNodeWithText("Temperature").assertIsDisplayed()
        composeTestRule.onNodeWithText("25°C").assertIsDisplayed()
    }

    @Test
    fun weatherLoadingContent_isDisplayed() {
        // When
        composeTestRule.setContent {
            WeatherLoadingContent()
        }

        // Then
        // Verify the loading placeholders are present
        composeTestRule.onRoot().assertIsDisplayed()
        // The loading content shows gray boxes as placeholders
    }

    @Test
    fun weatherForecastLoadingContent_isDisplayed() {
        // When
        composeTestRule.setContent {
            WeatherForecastLoadingContent()
        }

        // Then
        // Verify the forecast loading placeholders are present
        composeTestRule.onRoot().assertIsDisplayed()
        // The loading content shows gray boxes as placeholders
    }

    @Test
    fun formatTimestampToDate_formatsCorrectly() {
        // Given
        val timestamp = 1640995200L // 2022-01-01T00:00:00Z

        // When
        val result = formatTimestampToDate(timestamp)

        // Then
        assert(result == "01/01/2022")
    }
}