package com.benedetto.dev.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benedetto.data.repository.remote.WeatherRepository
import com.benedetto.data.util.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class RemoteViewModel @Inject constructor(private val weatherRepository: WeatherRepository) :
    ViewModel() {

    private val _weatherData = MutableStateFlow<UiState<String>>(UiState.Inactive)
    val weatherData: StateFlow<UiState<String>> = _weatherData.asStateFlow()

    fun getWeatherData(zipCode: String) {
        val time = measureTimeMillis {
            viewModelScope.launch {
                _weatherData.value = UiState.Loading
                weatherRepository.getWeather(zipCode)
                    .catch { exception ->
                        Log.e("RemoteViewModel", exception.message, exception)
                        _weatherData.value = UiState.Error(exception)
                    }
                    .buffer()
                    .collect { weather ->
                        _weatherData.value = UiState.Success("Success")
                    }
            }
        }
        _weatherData.value = UiState.Inactive
        log(time.toString())
    }

}

sealed class UiState<out T> {
    object Inactive : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val exception: Throwable) : UiState<Nothing>()
}