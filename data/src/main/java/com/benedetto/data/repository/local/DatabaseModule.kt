package com.benedetto.data.repository.local

import android.app.Application
import androidx.room.Room
import com.benedetto.data.repository.local.dao.WeatherDao
import com.benedetto.data.repository.local.dao.WeatherSummaryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Singleton
    @Provides
    internal fun provideDatabase(app: Application): WeatherDatabase {
        return Room.databaseBuilder(app, WeatherDatabase::class.java, "weather_database").build()
    }

    @Provides
    internal fun provideWeatherDao(db: WeatherDatabase): WeatherDao {
        return db.weatherDao()
    }

    @Provides
    internal fun provideWeatherSummaryDao(db: WeatherDatabase): WeatherSummaryDao {
        return db.weatherSummaryDao()
    }

}