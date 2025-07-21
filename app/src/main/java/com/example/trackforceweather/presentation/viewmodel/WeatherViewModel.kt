package com.example.trackforceweather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackforceweather.domain.model.LocationData
import com.example.trackforceweather.domain.model.Resource
import com.example.trackforceweather.domain.usecase.GetCachedForecastUseCase
import com.example.trackforceweather.domain.usecase.GetCachedWeatherUseCase
import com.example.trackforceweather.domain.usecase.GetWeatherForecastUseCase
import com.example.trackforceweather.domain.usecase.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase,
    private val getCachedWeatherUseCase: GetCachedWeatherUseCase,
    private val getCachedForecastUseCase: GetCachedForecastUseCase
) : ViewModel() {

    // Observer pattern implementation with StateFlow
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        observeCachedWeather()
        observeCachedForecast()
    }

    private fun observeCachedWeather() {
        getCachedWeatherUseCase.execute().onEach { cachedWeather ->
            if (cachedWeather != null) {
                _uiState.value = _uiState.value.copy(
                    weather = cachedWeather,
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun observeCachedForecast() {
        getCachedForecastUseCase.execute().onEach { weatherForecast ->
            if (weatherForecast != null) {
                _uiState.value = _uiState.value.copy(
                    forecast = weatherForecast,
                    isForecastLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    fun loadWeather(location: LocationData) {
        viewModelScope.launch {
            getWeatherUseCase.execute(location).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            weather = resource.data,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = resource.message
                        )
                    }
                }
            }
        }
    }

    fun loadWeatherForecast(location: LocationData) {
        viewModelScope.launch {
            getWeatherForecastUseCase.execute(location).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isForecastLoading = true,
                            forecastError = null
                        )
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            forecast = resource.data,
                            isForecastLoading = false,
                            forecastError = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isForecastLoading = false,
                            forecastError = resource.message
                        )
                    }
                }
            }
        }
    }

    fun refreshWeather(location: LocationData) {
        loadWeather(location)
        loadWeatherForecast(location)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearForecastError() {
        _uiState.value = _uiState.value.copy(forecastError = null)
    }
}