package com.benedetto.data.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.benedetto.data.repository.local.dao.WeatherDao
import com.benedetto.data.repository.local.dao.WeatherSummaryDao
import com.benedetto.data.repository.local.model.WeatherEntity
import com.benedetto.data.repository.local.model.WeatherSummary

@Database(
    entities = [WeatherEntity::class, WeatherSummary::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun weatherSummaryDao(): WeatherSummaryDao
}