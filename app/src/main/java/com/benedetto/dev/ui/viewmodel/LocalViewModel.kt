package com.benedetto.dev.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benedetto.data.repository.local.WeatherDbRepository
import com.benedetto.data.repository.local.WeatherSummaryDbRepository
import com.benedetto.data.repository.local.model.WeatherEntity
import com.benedetto.data.repository.local.model.WeatherSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalViewModel @Inject constructor(
    private val weatherDbRepository: WeatherDbRepository,
    private val weatherSummaryDbRepository: WeatherSummaryDbRepository
) :
    ViewModel() {

    private val weatherRecords = MutableStateFlow<List<WeatherEntity>>(emptyList())
    private val weatherSummaryRecords = MutableStateFlow<List<WeatherSummary>>(emptyList())

    val weatherEntity: StateFlow<List<WeatherEntity>> = weatherRecords.asStateFlow()
    val weatherSummaryEntity: StateFlow<List<WeatherSummary>> = weatherSummaryRecords.asStateFlow()

    fun loadWeather() {
        loadWeatherData()
        loadWeatherSummaryData()
    }

    private fun loadWeatherData() {
        viewModelScope.launch {
            weatherDbRepository.getWeatherData()
                .catch { exception ->
                    Log.e("loadWeatherData()", exception.message, exception)
                }
                .buffer()
                .collect { values ->
                    weatherRecords.value = values
                }
        }
    }

    private fun loadWeatherSummaryData() {
        viewModelScope.launch {
            weatherSummaryDbRepository.getWeatherSummaryDataFlow()
                .catch { exception ->
                    Log.e("loadWeatherSummaryData()", exception.message, exception)
                }
                .buffer()
                .collect { values ->
                    weatherSummaryRecords.value = values
                }
        }
    }
}

