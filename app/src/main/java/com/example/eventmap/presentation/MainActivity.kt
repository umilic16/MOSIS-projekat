package com.example.eventmap.presentation
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eventmap.data.User
import com.example.eventmap.presentation.theme.ui.EventMapTheme
import com.example.eventmap.presentation.utils.BottomNavBar
import com.example.eventmap.presentation.utils.BottomNavItem
import com.example.eventmap.presentation.utils.Navigation
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.presentation.viewmodels.MainActivityViewModel
import com.example.eventmap.utils.addRandomUsers
import com.example.eventmap.utils.getRandomString
import com.example.eventmap.utils.saveUser
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainActivityViewModel>()
    private val usersViewModel by viewModels<UsersViewModel>()
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //zbog testiranja
        //val auth = FirebaseAuth.getInstance()
        //auth.signOut()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationPermission()
        //addRandomUsers(3,6)
        setContent {
            EventMapTheme {
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    val backStateEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = backStateEntry?.destination?.route
                    Scaffold(
                        //kreiraj bottom bar
                        bottomBar = {
                            BottomNavBar(
                                //nevidljiv u login i register screen
                                showBar = currentRoute !in listOf("Login", "Register"),
                                items = listOf(
                                    BottomNavItem(
                                        name = "Home",
                                        route = "Home",
                                        icon = Icons.Default.Home
                                    ),
                                    BottomNavItem(
                                        name = "Map",
                                        route = "Map",
                                        icon = Icons.Default.Map
                                    ),
                                    BottomNavItem(
                                        name = "New Event",
                                        route = "CreateEvent",
                                        icon = Icons.Default.AddBox
                                    ),
                                    BottomNavItem(
                                        name = "Leaderboard",
                                        route = "Leaderboard",
                                        icon = Icons.Default.Leaderboard
                                    ),
                                    BottomNavItem(
                                        name = "Account",
                                        route = "Account",
                                        icon = Icons.Default.AccountCircle
                                    )
                                ),
                                navController = navController,
                                onItemClick = {
                                    //ako je kliknuto dugme na rutu na koju se nalazimo nista
                                    if (currentRoute != it.route) {
                                        navController.navigate(it.route)
                                    }
                                }
                            )
                        }) {
                        //navhost
                        Navigation(navController, viewModel, fusedLocationProviderClient, usersViewModel)
                    }
                }
            }
        }
    }

    private fun getLocationPermission() {
        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.permissionGrand(true)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        }
    }
}
