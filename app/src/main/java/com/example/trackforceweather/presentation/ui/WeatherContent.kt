package com.example.trackforceweather.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.trackforceweather.R
import com.example.trackforceweather.domain.model.Weather
import kotlin.math.roundToInt

@Composable
fun WeatherContent(weather: Weather) {
    val padding = dimensionResource(id = R.dimen.content_padding)
    val smallSpacing = dimensionResource(id = R.dimen.spacing_small)
    val cardElevation = dimensionResource(id = R.dimen.card_elevation_default)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // City name
        Text(
            text = weather.cityName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = smallSpacing)
        )

        // Weather icon and temperature
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = smallSpacing),
            elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
            ) {
                AsyncImage(
                    model = stringResource(R.string.weather_icon_url, weather.iconCode),
                    contentDescription = weather.description,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.weather_icon_size_medium))
                )

                Text(
                    text = stringResource(R.string.temperature_celsius, weather.temperature.roundToInt()),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = weather.description.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = smallSpacing)
                )
            }
        }

        // Weather details
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
        ) {
            Column(
                modifier = Modifier.padding(padding)
            ) {
                Text(
                    text = stringResource(R.string.details),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = smallSpacing)
                )

                WeatherDetailRow(
                    label = stringResource(R.string.feels_like),
                    value = stringResource(R.string.temperature_celsius, weather.feelsLike.roundToInt())
                )
                WeatherDetailRow(
                    label = stringResource(R.string.humidity),
                    value = stringResource(R.string.percentage_format, weather.humidity)
                )
                WeatherDetailRow(
                    label = stringResource(R.string.pressure),
                    value = stringResource(R.string.pressure_format, weather.pressure)
                )
                WeatherDetailRow(
                    label = stringResource(R.string.wind_speed),
                    value = stringResource(R.string.wind_speed_format, weather.windSpeed)
                )
            }
        }
    }
}

@Composable
fun WeatherDetailRow(label: String, value: String) {
    val verticalPadding = dimensionResource(id = R.dimen.spacing_extra_small)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun WeatherLoadingContent() {
    val smallPadding = dimensionResource(id = R.dimen.card_padding_small)
    val elevation = dimensionResource(id = R.dimen.card_elevation_default)
    val skeletonCircleSize = dimensionResource(id = R.dimen.weather_icon_size_medium)
    val skeletonTextHeight = dimensionResource(id = R.dimen.loading_text_height)
    val skeletonTextWidth = dimensionResource(id = R.dimen.loading_date_width)
    val cornerRadius = dimensionResource(id = R.dimen.corner_radius_extra_small)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(smallPadding),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(
            modifier = Modifier.padding(smallPadding),
            horizontalAlignment = Alignment.Start
        ) {
            repeat(3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = smallPadding),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(skeletonCircleSize)
                            .clip(CircleShape)
                            .background(Color.LightGray.copy(alpha = 0.5f))
                    )
                    Box(
                        modifier = Modifier
                            .width(skeletonTextWidth)
                            .height(skeletonTextHeight)
                            .clip(RoundedCornerShape(cornerRadius))
                            .background(Color.LightGray.copy(alpha = 0.5f))
                    )
                }
            }
        }
    }
}