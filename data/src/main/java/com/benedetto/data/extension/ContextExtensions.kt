package com.benedetto.data.extension

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast


fun Context.toast(message: CharSequence, duration: Int = Toast.LENGTH_LONG){
    Toast.makeText(this, message, duration).show()
}

fun Context.isNetworkAvailable(): Boolean{
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false

        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    } else {
        // if the android version is below M
        @Suppress("DEPRECATION") val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION") return networkInfo.isConnected
    }
}
