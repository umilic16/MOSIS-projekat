package com.example.eventmap.presentation.composables

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.eventmap.presentation.MainActivityViewModel
import com.example.eventmap.presentation.theme.ui.DefaultBlue
import com.example.eventmap.presentation.theme.ui.PaddingLarge
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.MapView
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import androidx.activity.viewModels
import androidx.compose.material.Surface
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.eventmap.data.User
import com.example.eventmap.presentation.theme.ui.DarkBlue
import com.example.eventmap.presentation.utils.rememberMapViewLifecycle
import com.example.eventmap.presentation.utils.saveUser
import com.example.eventmap.presentation.utils.updateLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.toObject
import com.google.maps.android.ktx.model.markerOptions

@Composable
fun MapView(navController: NavController, viewModel: MainActivityViewModel, fusedLocationProviderClient: FusedLocationProviderClient) {
    /*Box(modifier = Modifier
        .fillMaxSize()
        .padding(PaddingLarge), contentAlignment = Alignment.Center) {
    }*/
    getCurrentLocation(viewModel, fusedLocationProviderClient)
    val newDestinationSelected = viewModel.isNewLocationSelected.observeAsState(false)
    val destination = if (newDestinationSelected.value) LatLng(
        viewModel.selectedLat.value,
        viewModel.selectedLng.value
    ) else LatLng(43.32472, 21.90333)
    Surface(color = DarkBlue) {
        MapViewContainer(
            viewModel = viewModel,
            destination = destination,
        )

    }
}

@Composable
fun MapViewContainer(viewModel: MainActivityViewModel, destination: LatLng) {
    val mapView = rememberMapViewLifecycle()
    val context = LocalContext.current
    //Log.d("MAPDEBUG","MAPVIEWCONTAINER ENTER")
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView({ mapView }) {
            CoroutineScope(Dispatchers.Main).launch {
                val map = mapView.awaitMap()
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                }
                map.isMyLocationEnabled = true
                markUsers(map, viewModel)
                markEvents(map,viewModel)
                //Log.d("LOC_DEBUG", map.myLocation.toString())
               // Log.d("MAPDEBUG",map.toString())
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 6f))
                //Log.d("CurrLoc", "(${viewModel.userCurrentLat.value}, ${viewModel.userCurrentLng.value})")
                //trenutna lokacija
                /*map.addMarker(markerOptions {
                    this.title("Current location")
                    this.position(LatLng(viewModel.userCurrentLat.value,viewModel.userCurrentLng.value))
                })*/
                /*val markerOptions =
                    MarkerOptions().title("Start position").position(viewModel.pickUp)
                map.addMarker(markerOptions)

                val markerOptionsDestination =
                    MarkerOptions().title("Destination").position(destination)

                map.addMarker(markerOptionsDestination)

                map.addPolyline(
                    PolylineOptions().clickable(true).add(viewModel.pickUp, destination).width(4f)
                        .geodesic(true)
                )*/

                map.setOnMapLongClickListener { c ->
                    viewModel.getSelectedLocation(LatLng(c.latitude, c.longitude))
                    viewModel.updateSelectedLocation(true)
                }
            }
        }
    }
   // Log.d("MAPDEBUG","MAPVIEWCONTAINER EXIT")
}

fun getCurrentLocation(viewModel: MainActivityViewModel, fusedLocationProviderClient: FusedLocationProviderClient) {
    val auth = FirebaseAuth.getInstance()
    try {
        if (viewModel.locationPermissionGranted.value == true) {
            val task = fusedLocationProviderClient.lastLocation
            Log.d("Location_Result", task.toString())
            task.addOnSuccessListener {
                if (it != null) {
                    //Log.d("Location_Result", it.toString())
                    viewModel.currentUserGeoCoord(
                        LatLng(it.latitude, it.longitude)
                    )
                    //upisi lokaciju u bazu
                    updateLocation(auth.currentUser?.uid.toString(),  GeoPoint(it.latitude, it.longitude))
                }
            }
        }
    } catch (e: SecurityException) {
        Log.d("Exception", "Exception:  $e.message.toString()")

    }
}

fun markUsers(map: GoogleMap, viewModel: MainActivityViewModel){
    FirebaseFirestore.getInstance().collection("Users").get().addOnSuccessListener {
        for(document in it.documents){
           // Log.d("MARKERSDOC", document.toString())
            val email = document.data?.get("email")
            val location = document.getGeoPoint("location")
            if(location!=null) {
                //Log.d("MARKERSLOC", location.toString())
                //Log.d("MARKERSLL", "${email} LOCATION: ${location.latitude}${location.longitude}")
                map.addMarker(markerOptions {
                    this.title("User: ${email}")
                    this.position(LatLng(location.latitude, location.longitude))
                })
            }
        }
    }
}

fun markEvents(map: GoogleMap, viewModel: MainActivityViewModel){
    FirebaseFirestore.getInstance().collection("Events").get().addOnSuccessListener {
        for(document in it.documents){
            //Log.d("MARKERSDOC", document.toString())
            var title = document.data?.get("title")
            var desc = document.data?.get("description")
            val location = document.getGeoPoint("location")
            if(location!=null) {
                //Log.d("MARKERSLOC", location.toString())
                //Log.d("MARKERSLL", "${email} LOCATION: ${location.latitude}${location.longitude}")
                map.addMarker(markerOptions {
                    this.title("${title}: ${desc}")
                    this.position(LatLng(location.latitude, location.longitude))
                    this.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                })
            }
        }
    }
}