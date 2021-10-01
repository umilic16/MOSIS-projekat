package com.example.eventmap.presentation.utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eventmap.presentation.MainActivityViewModel
import com.example.eventmap.presentation.composables.*
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun Navigation(navController: NavController, viewModel: MainActivityViewModel, fusedLocationProviderClient: FusedLocationProviderClient) {
   // val navController = rememberNavController()
    NavHost(navController = navController as NavHostController,
        startDestination = if (checkIfLoggedIn()){
            "Login"
        }
        else{
            "Home"
        },
        builder = {
            composable(route = "Login", content = { Login(navController = navController) })
            composable(route = "Register", content = { Register(navController = navController) })
            composable(route = "Home", content = { Home(navController = navController, viewModel = viewModel) })
            composable(route = "Map", content = { MapView(navController = navController, viewModel = viewModel, fusedLocationProviderClient = fusedLocationProviderClient) })
            composable(route = "CreateEvent", content = { CreateEventView(navController = navController, viewModel = viewModel, fusedLocationProviderClient = fusedLocationProviderClient) })
            composable(route = "Leaderboard", content = { Leaderboard(navController = navController) })
            composable(route = "Account", content = { AccountView(navController = navController, viewModel = viewModel) })
        })
}