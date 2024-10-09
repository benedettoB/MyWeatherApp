package com.benedetto.data.repository.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_summary")
data class WeatherSummary(
    @PrimaryKey
    val startEpoch: Long,
    val dayOfWeek: String,
    val lowTemp: Double,
    val highTemp: Double,
    val windSummary: String,
    val description: String,
    val date: String,
    val location: String

) : Comparable<WeatherSummary> {

    override fun compareTo(other: WeatherSummary): Int {
        return compareValuesBy(this, other, WeatherSummary::startEpoch)
    }

}