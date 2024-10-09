package com.benedetto.data.repository.local

import com.benedetto.data.repository.local.dao.WeatherSummaryDao
import com.benedetto.data.repository.local.model.WeatherSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherSummaryDbRepository @Inject constructor(private val weatherSummaryDao: WeatherSummaryDao) {

    suspend fun addSummaryData(weatherSummary: WeatherSummary) {
        withContext(Dispatchers.IO) {
            weatherSummaryDao.insertWeatherSummary(weatherSummary)
        }
    }

    suspend fun removeOldWeatherSummaryData() {
        withContext(Dispatchers.IO) {
            weatherSummaryDao.deleteOldWeatherSummaryData()
        }
    }

    fun getWeatherSummaryDataFlow(): Flow<List<WeatherSummary>> = flow<List<WeatherSummary>> {
        emit(weatherSummaryDao.getWeatherSummary())
    }.flowOn(Dispatchers.IO)

}