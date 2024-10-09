package com.benedetto.data.repository.remote

import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeather(zipCode: String): Flow<String>
}