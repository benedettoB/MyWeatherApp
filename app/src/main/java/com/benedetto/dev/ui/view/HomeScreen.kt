package com.benedetto.dev.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.benedetto.data.extension.isNetworkAvailable
import com.benedetto.data.extension.isValidZipCode
import com.benedetto.data.util.log
import com.benedetto.dev.ui.WeatherApp
import com.benedetto.dev.ui.viewmodel.RemoteViewModel
import com.benedetto.dev.ui.viewmodel.UiState

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    remoteViewModel: RemoteViewModel = hiltViewModel()
) {
    val uiState by remoteViewModel.weatherData.collectAsState()
    if (!WeatherApp.appContext.isNetworkAvailable()) {
        navController.navigate("details")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val onButtonClick: (String) -> Boolean = { input ->
            remoteViewModel.getWeatherData(input)
            false
        }
        PageOneContent(modifier, onButtonClick)
        when (uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator(
                    modifier.padding(16.dp)
                )
            }

            is UiState.Success -> {
                navController.navigate("details")
            }

            is UiState.Error -> {
                val exception = (uiState as UiState.Error).exception
                Text(text = "Error: ${exception.message}")
            }

            is UiState.Inactive -> {
                log("Ui state is inactive")
            }
        }
    }
}

//Page 1 start
@Composable
fun PageOneContent(
    modifier: Modifier,
    onClickGetWeather: (String) -> Boolean
) {

    var zipCode by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var isButtonEnabled by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = zipCode,
        onValueChange = {
            //only allow up to 5 numeric characters
            if (it.length <= 5 && it.matches(Regex("^[0-9]*$"))) {
                zipCode = it
                showError = false
            }
            if (!it.isValidZipCode()) {
                showError = true
                isButtonEnabled = false
            } else {
                isButtonEnabled = true
            }

        },
        label = { Text("Enter ZIP code") },
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(0.8f), //Controls the width of the text field
        singleLine = true,
        maxLines = 1
    )
    if (showError) {
        Text(
            "Zip code must be exactly 5 digits",
            color = MaterialTheme.colorScheme.error
        )
    }
    Spacer(modifier = modifier.height((16.dp)))

    Button(
        onClick = {
            isButtonEnabled = onClickGetWeather(zipCode)
        },
        enabled = isButtonEnabled,
        modifier = modifier.fillMaxWidth(0.8f)
    ) {
        Text("Get Weather")
    }
}
