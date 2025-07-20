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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.trackforceweather.R
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
    val padding = dimensionResource(id = R.dimen.content_padding)
    val cardPadding = dimensionResource(id = R.dimen.card_padding_small)
    val cardElevation = dimensionResource(id = R.dimen.card_elevation_default)
    val emojiSize = 48.sp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(cardPadding),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
    ) {
        Column(
            modifier = Modifier.padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📍",
                fontSize = emojiSize,
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.spacing_small))
            )
            Text(
                text = stringResource(R.string.location_permission_required),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.spacing_extra_small))
            )
            Text(
                text = stringResource(
                    if (shouldShowRationale) R.string.location_permission_rationale
                    else R.string.location_permission_request_message
                ),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = padding)
            )
            Button(
                onClick = onRequestPermission,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.grant_permission))
            }
        }
    }
}

@Composable
fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    val padding = dimensionResource(id = R.dimen.content_padding)
    val cardPadding = dimensionResource(id = R.dimen.card_padding_small)
    val cardElevation = dimensionResource(id = R.dimen.card_elevation_default)
    val emojiSize = 48.sp
    val smallSpacing = dimensionResource(id = R.dimen.spacing_small)
    val extraSmallSpacing = dimensionResource(id = R.dimen.spacing_extra_small)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(cardPadding),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
    ) {
        Column(
            modifier = Modifier.padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚠️",
                fontSize = emojiSize,
                modifier = Modifier.padding(bottom = smallSpacing)
            )

            Text(
                text = stringResource(R.string.error),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(bottom = extraSmallSpacing)
            )

            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = smallSpacing)
            )

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.retry),
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}