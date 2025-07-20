package com.example.trackforceweather.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.example.trackforceweather.domain.model.WeatherForecast
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.floor
import kotlin.math.roundToInt

@Composable
fun WeatherForecastContent(forecast: WeatherForecast) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.card_padding_small)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(id = R.dimen.card_elevation_default)
        )
    ) {
        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.content_padding))) {
            Text(
                text = stringResource(id = R.string.forecast_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.spacing_small))
            )
            forecast.dailyForecasts.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(id = R.dimen.spacing_extra_small)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTimestampToDate(item.timestamp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    AsyncImage(
                        model = stringResource(
                            id = R.string.weather_icon_url,
                            item.iconCode
                        ),
                        contentDescription = item.description,
                        modifier = Modifier.size(dimensionResource(id = R.dimen.weather_icon_size_medium))
                    )
                    Text(
                        text = stringResource(
                            id = R.string.temperature_range_format,
                            floor(item.tempMin).toInt(),
                            item.tempMax.roundToInt()
                        ),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherForecastLoadingContent() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.card_padding_small)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(id = R.dimen.card_elevation_default)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.content_padding)),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(dimensionResource(id = R.dimen.loading_title_height))
                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)))
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.content_padding)))

            repeat(5) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(id = R.dimen.spacing_small)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(dimensionResource(id = R.dimen.loading_date_width))
                            .height(dimensionResource(id = R.dimen.loading_text_height))
                            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_extra_small)))
                            .background(Color.LightGray.copy(alpha = 0.5f))
                    )
                    Box(
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.weather_icon_size_medium))
                            .clip(CircleShape)
                            .background(Color.LightGray.copy(alpha = 0.5f))
                    )
                    Box(
                        modifier = Modifier
                            .width(dimensionResource(id = R.dimen.loading_temperature_width))
                            .height(dimensionResource(id = R.dimen.loading_text_height))
                            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_extra_small)))
                            .background(Color.LightGray.copy(alpha = 0.5f))
                    )
                }
            }
        }
    }
}

fun formatTimestampToDate(timestampSeconds: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        .withZone(ZoneId.systemDefault()) // or ZoneOffset.UTC if you want UTC

    return formatter.format(Instant.ofEpochSecond(timestampSeconds))
}