package com.example.trackforceweather.presentation.ui

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.trackforceweather.domain.model.LocationData
import com.example.trackforceweather.presentation.viewmodel.WeatherViewModel
import com.example.trackforceweather.utils.LocationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val hasPermission = remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            hasPermission.value = granted
            if (granted) {
                LocationUtils.getCurrentLocation(context) { location ->
                    location?.let {
                        viewModel.loadWeather(LocationData(it.latitude, it.longitude))
                        viewModel.loadWeatherForecast(LocationData(it.latitude, it.longitude))
                    }
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = uiState.isLoading || uiState.isForecastLoading,
        onRefresh = {
            if (hasPermission.value) {
                LocationUtils.getCurrentLocation(context) { location ->
                    location?.let {
                        viewModel.refreshWeather(LocationData(it.latitude, it.longitude))
                    }
                }
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!hasPermission.value) {
                LocationPermissionContent(
                    onRequestPermission = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    shouldShowRationale = true
                )
            } else {
                // Weather Section
                when {
                    uiState.isLoading && uiState.weather == null -> {
                        WeatherLoadingContent()
                    }
                    uiState.weather != null -> {
                        WeatherContent(weather = uiState.weather!!)
                        // In case we have cached data but got some error from API
                        if (uiState.error?.isNotEmpty() == true){
                            Toast.makeText(context, uiState.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                    uiState.error != null -> {
                        ErrorContent(
                            error = uiState.error!!,
                            onRetry = {
                                LocationUtils.getCurrentLocation(context) { location ->
                                    location?.let {
                                        viewModel.loadWeather(LocationData(it.latitude, it.longitude))
                                    }
                                }
                                viewModel.clearError()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Forecast Section
                when {
                    uiState.isForecastLoading &&
                            (uiState.forecast == null || uiState.forecast?.dailyForecasts.isNullOrEmpty()) -> {
                        WeatherForecastLoadingContent()
                    }
                    uiState.forecast != null && uiState.forecast?.dailyForecasts?.isNotEmpty() == true -> {
                        WeatherForecastContent(forecast = uiState.forecast!!)
                        // In case we have cached data but got some error from API
                        if (uiState.error?.isNotEmpty() == true){
                            Toast.makeText(context, uiState.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                    uiState.forecastError != null -> {
                        ErrorContent(
                            error = uiState.forecastError!!,
                            onRetry = {
                                LocationUtils.getCurrentLocation(context) { location ->
                                    location?.let {
                                        viewModel.loadWeatherForecast(LocationData(it.latitude, it.longitude))
                                    }
                                }
                                viewModel.clearForecastError()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LocationPermissionContent(
    onRequestPermission: () -> Unit,
    shouldShowRationale: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📍",
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Location Permission Required",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = if (shouldShowRationale) {
                    "This app needs location permission to show weather data for your current location. Please grant permission to continue."
                } else {
                    "To show weather data for your location, we need access to your device's location."
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = onRequestPermission,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Grant Permission")
            }
        }
    }
}

@Composable
fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚠️",
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Retry", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}