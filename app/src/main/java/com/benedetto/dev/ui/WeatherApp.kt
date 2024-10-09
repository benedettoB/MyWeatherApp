package com.benedetto.dev.ui

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WeatherApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this.applicationContext
    }

    //lateinit Variable: By using lateinit, we ensure that instance is always non-null after it's initialized in onCreate. This removes the need for null checks.
    companion object {
        lateinit var appContext: Context
            private set
    }
}