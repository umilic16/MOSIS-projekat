package com.example.eventmap.presentation
import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.Observer
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eventmap.components.BottomNavBar
import com.example.eventmap.components.BottomNavItem
import com.example.eventmap.presentation.theme.ui.EventMapTheme
import com.example.eventmap.presentation.utils.Navigation
import com.example.eventmap.presentation.utils.addUsersListener
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.services.FirebaseService.Companion.sharedPref
import com.example.eventmap.services.FirebaseService.Companion.token
import com.example.eventmap.services.TrackingService
import com.example.eventmap.utils.*
import com.example.eventmap.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.eventmap.utils.Constants.ACTION_STOP_SERVICE
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    private val usersViewModel by viewModels<UsersViewModel>()
    private var isFirstStart = true
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //FirebaseAuth.getInstance().signOut()
        //cloud messaging
        sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            token = it
        }
        getLocationPermission()
        //fused api za lokaciju
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        //initial value
        usersViewModel.loggedIn.postValue(checkIfLoggedIn())
        usersViewModel.loggedIn.observe(this, { isLoggedIn ->
            //Log.d("Login_Debug", "User logged in: $isLoggedIn")
            if(isLoggedIn){
                //procedura nakon logovanja
                setCurrentUser(usersViewModel)
                //register je sam postavlja jer je vec ucitao
                if(usersViewModel.picture != null){
                    setCurrentPicture(usersViewModel)
                }
                startOrResumeTrackingService(this)
                addUsersListener(usersViewModel)
                isFirstStart = false
            } else if(!isFirstStart){
                //procedura nakon sign out
                stopTrackingService(this)
                val registration = usersViewModel.listenerRegistration.value
                //ukloni users listener (trebalo bi da registration nije null ali nema veze)
                if(registration!=null){
                    registration.remove()
                }
                //reset view model
                usersViewModel.setAllUsers(null)
                usersViewModel.setCurrentUser(null)
                usersViewModel.setCurrentPicture(null)
            }
        })
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
                        Navigation(navController, fusedLocationProviderClient, usersViewModel)
                    }
                }
            }
        }
    }

    private fun getLocationPermission() {
        if (!checkIfHasLocationPermission(this)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        }
    }
}
