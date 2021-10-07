package com.example.eventmap.presentation
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eventmap.components.BottomNavBar
import com.example.eventmap.components.BottomNavItem
import com.example.eventmap.presentation.theme.ui.EventMapTheme
import com.example.eventmap.presentation.utils.GpsStatusListener
import com.example.eventmap.presentation.utils.LocationPermissionStatusListener
import com.example.eventmap.presentation.utils.Navigation
import com.example.eventmap.presentation.utils.addUsersListener
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.services.FirebaseService.Companion.sharedPref
import com.example.eventmap.services.FirebaseService.Companion.token
import com.example.eventmap.services.TrackingService.Companion.isServiceRunning
import com.example.eventmap.utils.Checkers.checkIfCanTrackLocation
import com.example.eventmap.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.example.eventmap.utils.DbAdapter.checkIfLoggedIn
import com.example.eventmap.utils.DbAdapter.setCurrentPictureUrl
import com.example.eventmap.utils.DbAdapter.setCurrentUser
import com.example.eventmap.utils.LocationUtil.hasLocationPermissions
import com.example.eventmap.utils.TrackingCommands.startOrResumeTrackingService
import com.example.eventmap.utils.TrackingCommands.stopTrackingService
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : ComponentActivity(), EasyPermissions.PermissionCallbacks {
    private val usersViewModel by viewModels<UsersViewModel>()
    private var isFirstStart = true
    companion object{
        lateinit var fusedLocationProviderClient: FusedLocationProviderClient
        lateinit var gpsStatusListener: GpsStatusListener
        lateinit var locationPermissionStatusListener: LocationPermissionStatusListener
    }
    //ne garantuje
    override fun onDestroy() {
        super.onDestroy()
        stopTrackingService(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //FirebaseAuth.getInstance().signOut()
        //try to find current user in firebase if not found its deleted so sign out
        sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            token = it
        }
        requestPermissions()
        //fused api za lokaciju
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        //gps observer
        gpsStatusListener = GpsStatusListener(this)
        locationPermissionStatusListener = LocationPermissionStatusListener(this)
        //tracking service takodje prati promene u statusu gps i permisija
        //zbog situacija kad je main activity stopiran
        gpsStatusListener.observe(this,{
            if(checkIfCanTrackLocation(this) && checkIfLoggedIn()){
                startOrResumeTrackingService(this)
            }else if(isServiceRunning){
                //da se ne bi pozvalo stop tracking service 2 puta
                // jednom ovde jednom u tracking servis observeru
                isServiceRunning = false
                stopTrackingService(this)
            }
        })
        //location permission observer
        locationPermissionStatusListener.observe(this,{
            if(checkIfCanTrackLocation(this) && checkIfLoggedIn()){
                startOrResumeTrackingService(this)
            }else if(isServiceRunning){
                isServiceRunning = false
                stopTrackingService(this)
            }
        })
        //initial value
        usersViewModel.loggedIn.postValue(checkIfLoggedIn())
        usersViewModel.loggedIn.observe(this, { isLoggedIn ->
            if(isLoggedIn){
                //google prompt za lokaciju
                showLocationPrompt()
                if(checkIfCanTrackLocation(this)){
                    startOrResumeTrackingService(this)
                }
                //procedura nakon logovanja
                setCurrentUser(usersViewModel)
                setCurrentPictureUrl(usersViewModel)
                addUsersListener(usersViewModel, this)
                //na prvi login se ucitavaju sve slike
                //setAllPictures(usersViewModel, this)
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
                usersViewModel.setCurrentPictureUrl(null)
            }
        })
        setContent {
            EventMapTheme {
                Surface(
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
                        Navigation(navController, usersViewModel)
                    }
                }
            }
        }
    }
    private fun requestPermissions() {
        if (hasLocationPermissions(this)) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permission to use this app",
                LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app",
                LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                //Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun showLocationPrompt() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val resolvable: ResolvableApiException = exception as ResolvableApiException
                            resolvable.startResolutionForResult(
                                this, LocationRequest.PRIORITY_HIGH_ACCURACY
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }
    //i nije potrebno
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LocationRequest.PRIORITY_HIGH_ACCURACY -> {
                if (resultCode == Activity.RESULT_OK) {
                    Log.e("Status_Debug: ","On")
                } else {
                    Log.e("Status_Debug: ","Off")
                }
            }
        }
    }
}
