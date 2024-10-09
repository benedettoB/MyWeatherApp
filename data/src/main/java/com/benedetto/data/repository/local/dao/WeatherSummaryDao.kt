package com.benedetto.data.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.benedetto.data.repository.local.model.WeatherSummary

@Dao
interface WeatherSummaryDao {

    @Insert
    suspend fun insertWeatherSummary(weatherSummary: WeatherSummary)

    @Query("SELECT * FROM weather_summary")
    suspend fun getWeatherSummary(): List<WeatherSummary>

    @Query("DELETE FROM weather_summary")
    suspend fun deleteOldWeatherSummaryData(): Int

}