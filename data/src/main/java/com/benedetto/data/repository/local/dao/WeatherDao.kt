package com.benedetto.data.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.benedetto.data.repository.local.model.WeatherEntity

@Dao
interface WeatherDao {
    @Insert
    suspend fun insertWeather(weatherEntity: WeatherEntity)

    @Query("SELECT * FROM weather_table")
    suspend fun getWeather(): List<WeatherEntity>

    @Query("DELETE FROM weather_table")
    suspend fun deleteOldWeatherData(): Int //return number of rows deleted, if no records, this will do nothing

}