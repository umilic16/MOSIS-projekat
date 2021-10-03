package com.example.eventmap.presentation.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.eventmap.presentation.viewmodels.MainActivityViewModel
import com.example.eventmap.presentation.utils.rememberMapViewLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.model.markerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun MapView(navController: NavController, fusedLocationProviderClient: FusedLocationProviderClient) {
    val mapView = rememberMapViewLifecycle()
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView({ mapView }) {
            CoroutineScope(Dispatchers.Main).launch {
                val map = mapView.awaitMap()
                map.isMyLocationEnabled = true
                fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude,it.longitude),12f))
                }
                //markUsers(map, viewModel)
                //markEvents(map,viewModel)
                //
            }
        }
    }
    //
}

/*fun getCurrentLocation(viewModel: MainActivityViewModel, fusedLocationProviderClient: FusedLocationProviderClient) {
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
        Log.d("Exception", "Exception:  $e.message")

    }
}*/

/*fun markUsers(map: GoogleMap, viewModel: MainActivityViewModel){
    FirebaseFirestore.getInstance().collection("Users").get().addOnSuccessListener {
        for(document in it.documents){
           // Log.d("MARKERSDOC", document.toString())
            val email = document.data?.get("email")
            val location = document.getGeoPoint("location")
            if(location!=null) {
                //Log.d("MARKERSLOC", location.toString())
                //Log.d("MARKERSLL", "$email LOCATION: $location.latitude, $location.longitude")
                map.addMarker(markerOptions {
                    this.title("User: $email")
                    this.position(LatLng(location.latitude, location.longitude))
                })
            }
        }
    }
}*/

fun markEvents(map: GoogleMap, viewModel: MainActivityViewModel){
    FirebaseFirestore.getInstance().collection("Events").get().addOnSuccessListener {
        for(document in it.documents){
            val title = document.data?.get("title")
            val desc = document.data?.get("description")
            val location = document.getGeoPoint("location")
            if(location!=null) {
                map.addMarker(markerOptions {
                    this.title("$title: $desc")
                    this.position(LatLng(location.latitude, location.longitude))
                    this.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                })
            }
        }
    }
}