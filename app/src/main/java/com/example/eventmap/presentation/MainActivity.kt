package com.example.eventmap.presentation
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eventmap.components.BottomNavBar
import com.example.eventmap.components.BottomNavItem
import com.example.eventmap.presentation.theme.ui.EventMapTheme
import com.example.eventmap.presentation.utils.GpsStatusListener
import com.example.eventmap.presentation.utils.Navigation
import com.example.eventmap.presentation.utils.PermissionStatusListener
import com.example.eventmap.presentation.utils.addUsersListener
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.services.FirebaseService.Companion.sharedPref
import com.example.eventmap.services.FirebaseService.Companion.token
import com.example.eventmap.utils.*
import com.example.eventmap.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.example.eventmap.utils.LocationUtil.hasLocationPermissions
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
    }
    /*override fun onPause() {
        super.onPause()
        Log.d("Act_Debug", "Pausing main")
    }*/
    /*override fun onResume() {
        super.onResume()
        //user ne moze da iskljuci permisije za lokaciju bez da se pozove onPause
        //pa se u onResume proveravaju permisije opet
        requestPermissions()
        Log.d("Act_Debug", "Resuming main")
    }*/
    /*override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.d("Act_Debug", "Main activity has focus: $hasFocus")
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            token = it
        }
        requestPermissions()
        //fused api za lokaciju
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        //gps observer
        GpsStatusListener(this).observe(this,{
            if(checkIfCanTrackLocation(this)){
                startOrResumeTrackingService(this)
            }else{
                stopTrackingService(this)
            }
        })
        //location permission observer
        PermissionStatusListener(this).observe(this,{
            if(checkIfCanTrackLocation(this)){
                startOrResumeTrackingService(this)
            }else{
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
                }else {
                    Toast.makeText(this,"Can't track your location!", Toast.LENGTH_SHORT)
                }
                //dijalog da se ukljuci lokacija
                //procedura nakon logovanja
                setCurrentUser(usersViewModel)
                //register je sam postavlja jer je vec ucitao
                if(usersViewModel.picture != null){
                    setCurrentPicture(usersViewModel)
                }
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

    /*private fun checkGpsNetwork() {
        val lm = this.getSystemService(LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder(this)
                .setMessage("Gps and network aren't enabled")
                .setPositiveButton("Open location settings",
                    DialogInterface.OnClickListener { paramDialogInterface, paramInt ->
                        this.startActivity(
                            Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS
                            )
                        )
                    })
                .setNegativeButton("Cancel", null)
                .show()
        }
    }*/

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
