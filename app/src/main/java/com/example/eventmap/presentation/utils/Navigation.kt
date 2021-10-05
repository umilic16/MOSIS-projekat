package com.example.eventmap.presentation.utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eventmap.presentation.composables.*
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.utils.checkIfLoggedIn
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun Navigation(navController: NavController, usersViewModel: UsersViewModel) {
   // val navController = rememberNavController()
    NavHost(navController = navController as NavHostController,
        startDestination = if (checkIfLoggedIn()){
            "Home"
        }
        else{
            "Login"
        },
        builder = {
            composable(route = "Login", content = { Login(navController = navController, viewModel = usersViewModel) })
            composable(route = "Register", content = { Register(navController = navController, viewModel = usersViewModel) })
            composable(route = "Home", content = { Home(navController = navController, viewModel = usersViewModel) })
            composable(route = "Map", content = { MapView(navController = navController) })
            composable(route = "CreateEvent", content = { CreateEventView(navController = navController) })
            composable(route = "Leaderboard", content = { Leaderboard(navController = navController, usersViewModel) })
            composable(route = "Account", content = { AccountView(navController = navController, viewModel = usersViewModel) })
        })
}