package com.benedetto.dev.ui.view

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.benedetto.data.repository.local.model.WeatherEntity
import com.benedetto.data.repository.local.model.WeatherSummary
import com.benedetto.data.util.isToday
import com.benedetto.dev.R
import com.benedetto.dev.ui.viewmodel.LocalViewModel

@Composable
fun DetailsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    localViewModel: LocalViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        localViewModel.loadWeather()
    }
    val weather by localViewModel.weatherEntity.collectAsState()
    val weatherSummary by localViewModel.weatherSummaryEntity.collectAsState()

    PageTwoContent(modifier, weatherReport = weather, weatherSummaryReport = weatherSummary)
}

@Composable
private fun PageTwoContent(
    modifier: Modifier,
    weatherReport: List<WeatherEntity>,
    weatherSummaryReport: List<WeatherSummary>
) {
    LazyColumn(modifier.padding(vertical = 4.dp)) {
        items(items = weatherSummaryReport) { weatherSummaryItem ->
            Card(weatherSummaryItem = weatherSummaryItem, weatherReport = weatherReport, modifier)
        }
    }
}

@Composable
private fun Card(
    weatherSummaryItem: WeatherSummary,
    weatherReport: List<WeatherEntity>,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        CardContent(
            weatherSummaryItem = weatherSummaryItem,
            weatherReport = weatherReport,
            modifier = modifier
        )
    }
}

@Composable
private fun CardContent(
    weatherSummaryItem: WeatherSummary,
    weatherReport: List<WeatherEntity>,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(12.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {


            val day: String =
                if (isToday(weatherSummaryItem.startEpoch)) "Today" else weatherSummaryItem.dayOfWeek
            Text(
                text = "${day} ${weatherSummaryItem.lowTemp}F - ${weatherSummaryItem.highTemp}F\n${weatherSummaryItem.description}\n${weatherSummaryItem.windSummary}",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
            if (expanded) {
                weatherReport.forEach {
                    val startEpoch: Long = weatherSummaryItem.startEpoch
                    if (it.startEpoch == startEpoch) {
                        Text(
                            text =
                            "Time: ${it.formattedEpoch}\n" +
                                    "Temp: ${it.temp}F \n" +
                                    "Wind Speed: ${it.windSpeed} ${it.windDirection}\n" +
                                    "Description: ${it.description}\n",
                        )
                    }
                }
                Text(text = "Report generated ${weatherSummaryItem.date} for ${weatherSummaryItem.location} ")
            }
        }
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = if (expanded) Filled.ExpandLess else Filled.ExpandMore,
                contentDescription = if (expanded) {
                    stringResource(R.string.show_less)
                } else {
                    stringResource(R.string.show_more)
                }
            )
        }
    }
}