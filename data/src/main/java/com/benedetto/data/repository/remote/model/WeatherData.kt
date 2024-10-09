package com.benedetto.data.repository.remote.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Parcelize
@Serializable
data class WeatherData(
    val cod: String = "", //http response code: 200
    val message: Int = 0, //http msg
    val cnt: Int = 0, //count for number of records
    val list: ArrayList<WeatherListItem> = arrayListOf(), // list indexed by dt epoch time stamp in seconds
    val city: City = City()
) : Parcelable

val withUnknownKeys = Json { ignoreUnknownKeys = true }

@Parcelize
@Serializable
data class WeatherListItem(
    val dt: Int = 0, //epoch time stamp in seconds
    val main: Temperature = Temperature(),
    val weather: ArrayList<Weather> = arrayListOf(), // Weather.description for : sunny, partly cloudy etc
    val wind: Wind = Wind(),
) : Parcelable

@Parcelize
@Serializable
data class Temperature(
    val temp: Double = 0.0,
) : Parcelable

@Parcelize
@Serializable
data class Weather(
    val description: String = "", //description for : sunny, partly cloudy etc
) : Parcelable

@Parcelize
@Serializable
data class Wind(
    val speed: Double = 0.0,// 20 mph
    val deg: Int = 0, //enables us to calculate the direction the wind is blowing
) : Parcelable

@Parcelize
@Serializable
data class City(
    val name: String = "",
) : Parcelable
