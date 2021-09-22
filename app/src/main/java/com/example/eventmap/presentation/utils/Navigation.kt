package com.example.eventmap.presentation.utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventmap.presentation.login.Login
import com.example.eventmap.presentation.register.Register

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = "Login",
        builder = {
            composable(route = "Login", content = { Login(navController = navController) })
            composable(route = "Register", content = { Register(navController = navController) })
        })
}