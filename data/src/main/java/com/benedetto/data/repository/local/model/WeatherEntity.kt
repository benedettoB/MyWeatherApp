package com.benedetto.data.repository.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_table")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val startEpoch: Long,
    val epoch: Long,
    val formattedEpoch: String,
    val temp: Double,
    val description: String,
    val windSpeed: Double,
    val windDeg: Int,
    val windDirection: String,
    val dayOfWeek: String,
    val location: String
)
