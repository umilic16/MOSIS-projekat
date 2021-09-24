package com.example.eventmap.presentation.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventmap.presentation.MainActivityViewModel
import com.example.eventmap.presentation.composables.AccountView
import com.example.eventmap.presentation.composables.CreateEventView
import com.example.eventmap.presentation.composables.Leaderboard
import com.example.eventmap.presentation.composables.MapView
import com.example.eventmap.presentation.composables.Login
import com.example.eventmap.presentation.composables.Register
import com.example.eventmap.presentation.composables.Home
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
            composable(route = "Home", content = { Home(navController = navController) })
            composable(route = "Map", content = { MapView(navController = navController, viewModel = viewModel, fusedLocationProviderClient = fusedLocationProviderClient) })
            composable(route = "CreateEvent", content = { CreateEventView(navController = navController, viewModel = viewModel) })
            composable(route = "Leaderboard", content = { Leaderboard(navController = navController) })
            composable(route = "Account", content = { AccountView(navController = navController) })
        })
}