package com.benedetto.data.repository.local

import com.benedetto.data.repository.local.dao.WeatherDao
import com.benedetto.data.repository.local.model.WeatherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherDbRepository @Inject constructor(private val weatherDao: WeatherDao) {

    suspend fun addWeatherData(weatherEntity: WeatherEntity) {
        withContext(Dispatchers.IO) {
            weatherDao.insertWeather(weatherEntity)
        }
    }

    fun getWeatherData(): Flow<List<WeatherEntity>> = flow {
        emit(weatherDao.getWeather())
    }.flowOn(Dispatchers.IO)

    suspend fun removeOldWeatherData() {
        withContext(Dispatchers.IO) {
            weatherDao.deleteOldWeatherData()
        }
    }

    suspend fun getWeatherList(): List<WeatherEntity> = withContext(Dispatchers.IO) {
        weatherDao.getWeather()
    }
}