package com.benedetto.dev.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.benedetto.dev.ui.view.DetailsScreen
import com.benedetto.dev.ui.view.HomeScreen
import com.benedetto.dev.ui.viewmodel.LocalViewModel
import com.benedetto.dev.ui.viewmodel.RemoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigate(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val remoteViewModel: RemoteViewModel = hiltViewModel()
    val localViewModel: LocalViewModel = hiltViewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WeatherApp") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { paddingValues ->
            Surface(
                modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.background
            ) {
                //content
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(navController, modifier, remoteViewModel)
                    }
                    composable("details") {
                        DetailsScreen(navController, modifier, localViewModel)
                    }
                }
            }
        }
    )
}