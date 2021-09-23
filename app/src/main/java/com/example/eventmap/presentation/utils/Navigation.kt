package com.example.eventmap.presentation.utils

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventmap.presentation.composables.AccountView
import com.example.eventmap.presentation.composables.CreateEventView
import com.example.eventmap.presentation.composables.Leaderboard
import com.example.eventmap.presentation.composables.MapView
import com.example.eventmap.presentation.composables.Login
import com.example.eventmap.presentation.composables.Register
import com.example.eventmap.presentation.composables.Home

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController,
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
            composable(route = "Map", content = { MapView(navController = navController) })
            composable(route = "CreateEvent", content = { CreateEventView(navController = navController) })
            composable(route = "Leaderboard", content = { Leaderboard(navController = navController) })
            composable(route = "Account", content = { AccountView(navController = navController) })
        })
}