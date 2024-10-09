package com.benedetto.data.util

import android.util.Log
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun log(msg: String) = Log.d("MyWeatherApp", "Msg: $msg Thread:[${Thread.currentThread().name}]")

fun isToday(epochSeconds: Long): Boolean {
    val instant = Instant.ofEpochSecond(epochSeconds)
    val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    val today: LocalDate = LocalDate.now()
    return today.isEqual(localDate)
}