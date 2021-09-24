package com.example.eventmap.presentation
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eventmap.presentation.composables.getCurrentLocation
import com.example.eventmap.presentation.theme.ui.EventMapTheme
import com.example.eventmap.presentation.utils.BottomNavBar
import com.example.eventmap.presentation.utils.BottomNavItem
import com.example.eventmap.presentation.utils.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.maps.model.LatLng

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainActivityViewModel>()
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //zbog testiranja
        //val auth = FirebaseAuth.getInstance()
        //auth.signOut()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationPermission()
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
                        /*topBar={
                            TopAppBar(
                                backgroundColor = DefaultBlue,
                                contentColor = DefaultWhite,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(fraction = 0.1f),
                                contentPadding = PaddingValues(1.dp)
                            ) {
                                Text(text = "EventMap", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        },*/
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
                                        icon = Icons.Default.PostAdd
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
                        Navigation(navController, viewModel, fusedLocationProviderClient, this)
                    }
                }
            }
        }
    }

    private fun getLocationPermission() {
        //val task = fusedLocationProviderClient.lastLocation
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
            /*task.addOnSuccessListener {
                if(it!=null){
                    Log.d("LocDebug", "${it.longitude},${it.latitude}")
                }
            }*/

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        }
    }
}
