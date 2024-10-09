package com.benedetto.data.repository.remote

import com.benedetto.data.repository.local.WeatherDbRepository
import com.benedetto.data.repository.local.WeatherSummaryDbRepository
import com.benedetto.data.repository.local.model.WeatherEntity
import com.benedetto.data.repository.local.model.WeatherSummary
import com.benedetto.data.repository.remote.model.WeatherData
import com.benedetto.data.repository.remote.model.withUnknownKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.SortedMap
import java.util.TreeMap
import javax.inject.Inject

internal class WeatherRepositoryImpl @Inject constructor(
    private val weatherDbRepository: WeatherDbRepository,
    private val weatherSummaryDbRepository: WeatherSummaryDbRepository
) : WeatherRepository {

    private val client = OkHttpClient()

    override fun getWeather(zipCode: String): Flow<String> = flow {
        val data = getWeatherData(zipCode)
        processWeatherData(data)
        emit("Success")
    }.flowOn(Dispatchers.IO)

    private fun getWeatherData(zipCode: String): WeatherData {

        val request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/forecast?zip=${zipCode},us&units=imperial&appid=84bb5c755ab4ce5f6179b6e0cf6ec3ed")
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            if (responseBody != null) {
                return withUnknownKeys.decodeFromString<WeatherData>(responseBody)
            } else {
                throw IOException("Response body is null")
            }
        } else {
            throw IOException("Unexpected code $response")
        }
    }

    private suspend fun processWeatherData(weatherData: WeatherData) {
        weatherDbRepository.removeOldWeatherData()
        for (weather in weatherData.list) {
            val epoch: Long = weather.dt.toLong()
            val startEpoch: Long = getDayStartEpoch(epoch)
            val formattedEpochString = getLocalDateTime(epoch)
            val temp: Double = weather.main.temp
            val description: String = weather.weather[0].description
            val windSpeed: Double = weather.wind.speed
            val windDeg: Int = weather.wind.deg
            val windDirection: String = getWindDirection(windDeg.toDouble())
            val dayOfWeek: String = getDayOfWeek(epoch)
            val location: String = weatherData.city.name
            val weatherEntity = WeatherEntity(
                0,
                startEpoch,
                epoch,
                formattedEpochString,
                temp,
                description,
                windSpeed,
                windDeg,
                windDirection,
                dayOfWeek,
                location
            )
            weatherDbRepository.addWeatherData(weatherEntity)
        }
        Summary().begin()
    }

    private inner class Summary() {
        //TreeMap to store day (as epoch seconds at midnight) and corresponding list of WeatherEntity
        private val recordsByDay: SortedMap<Long, MutableList<WeatherEntity>> = TreeMap()

        suspend fun begin() {
            weatherSummaryDbRepository.removeOldWeatherSummaryData()
            val recordsToAdd: List<WeatherEntity> = weatherDbRepository.getWeatherList()

            recordsToAdd.forEach {
                addRecord(it)
            }

            recordsByDay.forEach { (dayStart, records) ->
                val startEpoch: Long = dayStart
                val dayOfWeek: String = getDayOfWeek(startEpoch)
                val lowTemp: Double = records.minOf { it.temp }
                val highTemp: Double = records.maxOf { it.temp }
                val lowWindSpeed: Double = records.minOf { it.windSpeed }
                val highWindSpeed: Double = records.maxOf { it.windSpeed }
                val windDirection: String = findMostFrequentWindDirection(records)
                val windSummary = "$lowWindSpeed - $highWindSpeed $windDirection"
                val description: String = findMostFrequentDescription(recordsToAdd)
                val date: String = getFormattedLocalDate(startEpoch)
                val location: String = records.first().location
                val weatherSummary =
                    WeatherSummary(
                        startEpoch,
                        dayOfWeek,
                        lowTemp,
                        highTemp,
                        windSummary,
                        description,
                        date,
                        location
                    )
                weatherSummaryDbRepository.addSummaryData(weatherSummary)
            }
        }

        fun addRecord(weatherEntity: WeatherEntity) {
            val dayStart = weatherEntity.startEpoch
            val records = recordsByDay.getOrPut(dayStart) { mutableListOf() }
            records.add(weatherEntity)
        }
    }

    //Function to get the start of the day (midnight) for the given epoch seconds
    private fun getDayStartEpoch(epochSeconds: Long): Long {
        val instant = Instant.ofEpochSecond(epochSeconds)
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
        return localDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
    }

    private fun getWindDirection(degree: Double): String {
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        val index = ((degree + 22.5) / 45).toInt() % 8
        return directions[index]
    }

    private fun getDayOfWeek(epochSeconds: Long): String {
        val instant: Instant = Instant.ofEpochSecond(epochSeconds)
        val zoneId: ZoneId = ZoneId.systemDefault()
        val localDate: LocalDate = instant.atZone(zoneId).toLocalDate()
        return localDate.dayOfWeek.toString()
    }

    private fun getLocalDateTime(epochSeconds: Long): String {
        val instant: Instant = Instant.ofEpochSecond(epochSeconds)
        val zoneId: ZoneId = ZoneId.systemDefault()
        val localDateTime: LocalDateTime = instant.atZone(zoneId).toLocalDateTime()
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return localDateTime.format(formatter)
    }

    private fun getFormattedLocalDate(epochSeconds: Long): String {
        val instant: Instant = Instant.ofEpochSecond(epochSeconds)
        val zoneId: ZoneId = ZoneId.systemDefault()
        val localDateTime: LocalDateTime = instant.atZone(zoneId).toLocalDateTime()
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return localDateTime.format(formatter)
    }


    private fun findMostFrequentDescription(list: List<WeatherEntity> = ArrayList()): String {
        val frequencyMap = mutableMapOf<String, Int>()
        for (weather in list) {
            frequencyMap[weather.description] =
                frequencyMap.getOrDefault(weather.description, 0) + 1
        }
        return frequencyMap.maxBy { it.value }.key
    }

    private fun findMostFrequentWindDirection(list: List<WeatherEntity> = ArrayList()): String {
        val frequencyMap = mutableMapOf<String, Int>()
        for (weather in list) {
            frequencyMap[weather.windDirection] =
                frequencyMap.getOrDefault(weather.description, 0) + 1
        }
        return frequencyMap.maxBy { it.value }.key
    }

}